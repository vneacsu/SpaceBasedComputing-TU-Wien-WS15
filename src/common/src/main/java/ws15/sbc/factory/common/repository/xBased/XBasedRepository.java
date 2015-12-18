package ws15.sbc.factory.common.repository.xBased;

import com.rabbitmq.client.*;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.*;
import ws15.sbc.factory.common.repository.EntityMatcher;
import ws15.sbc.factory.common.repository.Repository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.apache.commons.collections.ListUtils.synchronizedList;
import static org.apache.commons.lang.SerializationUtils.serialize;
import static ws15.sbc.factory.common.repository.xBased.Event.ActionType.STORED;
import static ws15.sbc.factory.common.repository.xBased.Event.ActionType.TAKEN;

@Singleton
public class XBasedRepository implements Repository {

    private static final Logger log = LoggerFactory.getLogger(XBasedRepository.class);

    private final String exchange;
    private final String eventsRoutingKey;

    private final Channel channel;
    private final XBasedTxManager txManager;

    private Optional<String> eventQueueName = Optional.empty();
    private List<ConditionalConsumerDecorator<? extends Serializable>> storedEventConsumers = synchronizedList(new ArrayList<>());
    private List<ConditionalConsumerDecorator<? extends Serializable>> takenEventConsumers = synchronizedList(new ArrayList<>());

    @Inject
    public XBasedRepository(Channel channel, XBasedTxManager txManager) {
        exchange = "factory-exchange";
        eventsRoutingKey = "factory-events";

        this.channel = channel;
        this.txManager = txManager;

        try {
            initializeExchange();
            initializeQueues();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeExchange() throws IOException {
        log.info("Declaring exchange {}", exchange);

        channel.exchangeDeclare(exchange, "topic");
    }

    private void initializeQueues() {
        getQueueTypes()
                .map(this::getQueueNameFor)
                .forEach(it -> {
                    log.info("Declaring queue {} bound to exchange {}", it, exchange);

                    try {
                        channel.queueDeclare(it, false, false, false, null);

                        channel.queueBind(it, exchange, it);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private Stream<Class<? extends Component>> getQueueTypes() {
        return asList(Engine.class, Rotor.class, Casing.class, ControlUnit.class,
                EngineRotorPair.class, Carcase.class, Drone.class).stream();
    }

    @Override
    public void storeEntity(Serializable entity) {
        log.info("Publishing entity {} to exchange {}", entity, exchange);

        publish(serialize(entity), getQueueNameFor(entity.getClass()));
        notifyAction(entity, STORED);

        commitIfImplicitTransaction();
    }

    private String getQueueNameFor(Class<? extends Serializable> entityClass) {
        return entityClass.getSimpleName();
    }

    private void publish(byte[] content, String... routingKeys) {
        try {
            for (String routingKey : routingKeys) {
                channel.basicPublish(exchange, routingKey, null, content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void notifyAction(Serializable entity, Event.ActionType actionType) {
        byte[] eventContent = serialize(new Event<>(entity, actionType));
        publish(eventContent, eventsRoutingKey);
    }

    private void commitIfImplicitTransaction() {
        if (!txManager.isTransactionActive()) {
            txManager.commit();
        }
    }

    @Override
    public void storeEntities(List<? extends Serializable> entities) {
        entities.forEach(this::storeEntity);
    }

    @Override
    public <T extends Serializable> Optional<T> takeOne(EntityMatcher<T> matcher) {
        log.info("Getting 1 entity matching {}", matcher);

        Optional<T> entity = getFirstMatching(matcher);
        if (!entity.isPresent()) {
            log.info("Empty response for matcher {}", matcher);
            return Optional.empty();
        }

        log.info("Got entity {}", entity);

        notifyAction(entity.get(), TAKEN);

        commitIfImplicitTransaction();

        return entity;
    }

    private <T extends Serializable> Optional<T> getFirstMatching(EntityMatcher<T> matcher) {
        GetResponse response;

        do {
            try {
                response = channel.basicGet(getQueueNameFor(matcher.getEntityClass()), false);

                if (response != null) {
                    Serializable entity = (Serializable) SerializationUtils.deserialize(response.getBody());
                    if (matcher.matches(entity)) {
                        channel.basicAck(response.getEnvelope().getDeliveryTag(), false);

                        return Optional.of((T) entity); //guarded by matcher
                    } else {
                        channel.basicNack(response.getEnvelope().getDeliveryTag(), false, true);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (response != null);

        return Optional.empty();
    }

    @Override
    public <T extends Serializable> Optional<List<T>> take(EntityMatcher<T> matcher, int count) {
        boolean isTransactionActive = txManager.isTransactionActive();
        if (!isTransactionActive) {
            txManager.beginTransaction();
        }

        List<T> entities = new ArrayList<>();

        for (int i = 0; i < count; ++i) {
            Optional<T> entity = takeOne(matcher);

            if (!entity.isPresent()) {
                if (!isTransactionActive) {
                    txManager.rollback();
                }
                return Optional.empty();
            } else {
                entities.add(entity.get());
            }
        }

        if (!isTransactionActive) {
            txManager.commit();
        }

        return Optional.of(entities);
    }

    @Override
    public <T extends Serializable> void onEntityStored(EntityMatcher<T> matcher, Consumer<T> consumer) {
        log.info("Registering listener for entity stored events");

        storedEventConsumers.add(new ConditionalConsumerDecorator<>(consumer, matcher));

        startEventQueueIfNotActive();
    }

    private void startEventQueueIfNotActive() {
        log.info("Starting event queue consumer if not active");

        if (eventQueueName.isPresent()) {
            log.info("Event queue already active, using existent instance");
            return;
        }

        eventQueueName = Optional.of(prepareEventQueue());

        try {
            channel.basicConsume(eventQueueName.get(), prepareEventQueueConsumer());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String prepareEventQueue() {
        log.info("Preparing event queue for routing key {}", eventsRoutingKey);

        try {
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchange, eventsRoutingKey);

            return queueName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private com.rabbitmq.client.Consumer prepareEventQueueConsumer() {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Event event = (Event) SerializationUtils.deserialize(body);

                log.info("Queue consumer woken up for {} with action {}", event.getEntity(), event.getActionType());

                if (event.getActionType() == STORED) {
                    storedEventConsumers.forEach(it -> it.accept(event.getEntity()));
                } else if (event.getActionType() == TAKEN) {
                    takenEventConsumers.forEach(it -> it.accept(event.getEntity()));
                }
            }
        };
    }

    @Override
    public <T extends Serializable> void onEntityTaken(EntityMatcher<T> matcher, Consumer<T> consumer) {
        log.info("Registering listener for entity taken events");

        takenEventConsumers.add(new ConditionalConsumerDecorator<>(consumer, matcher));

        startEventQueueIfNotActive();
    }

}

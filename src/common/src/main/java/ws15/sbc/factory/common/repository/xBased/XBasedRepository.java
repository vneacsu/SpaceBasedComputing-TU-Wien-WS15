package ws15.sbc.factory.common.repository.xBased;

import com.google.common.base.Preconditions;
import com.rabbitmq.client.*;
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
import static org.apache.commons.lang.SerializationUtils.deserialize;
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
                .forEach(this::declareQueue);
    }

    private Stream<Class<? extends Component>> getQueueTypes() {
        return asList(Engine.class, Rotor.class, Casing.class, ControlUnit.class,
                EngineRotorPair.class, Carcase.class, Drone.class).stream();
    }

    private void declareQueue(String queue) {
        log.info("Declaring queue {} bound to exchange {}", queue, exchange);

        try {
            channel.queueDeclare(queue, false, false, false, null);

            channel.queueBind(queue, exchange, queue);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void storeEntity(Serializable entity) {
        log.info("Publishing entity {} to exchange {}", entity, exchange);

        publish(serialize(entity), getQueueNameFor(entity.getClass()));
        notifyAction(entity, STORED);

        commitIfImplicitTransaction();
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

    private String getQueueNameFor(Class<? extends Serializable> entityClass) {
        return entityClass.getSimpleName();
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
        Preconditions.checkState(txManager.isTransactionActive(), "takeOne requires active transaction");

        log.info("Getting 1 entity matching {}", matcher);

        Optional<T> entity = getFirstMatching(matcher);
        if (!entity.isPresent()) {
            log.info("Empty response for matcher {}", matcher);
            return Optional.empty();
        }

        log.info("Got entity {}", entity);

        notifyAction(entity.get(), TAKEN);

        return entity;
    }

    private <T extends Serializable> Optional<T> getFirstMatching(EntityMatcher<T> matcher) {
        for (GetResponse response = getNextPotentialMatching(matcher); response != null; response = getNextPotentialMatching(matcher)) {
            Serializable entity = (Serializable) deserialize(response.getBody());

            if (matcher.matches(entity)) {
                ackGetResponse(response);

                return Optional.of((T) entity); //guarded by matcher
            } else {
                log.info("Entity {} not matching {}. It will be requeued", entity, matcher);
                nackGetResponse(response);
            }
        }

        return Optional.empty();
    }

    private <T extends Serializable> GetResponse getNextPotentialMatching(EntityMatcher<T> matcher) {
        try {
            return channel.basicGet(getQueueNameFor(matcher.getEntityClass()), false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void ackGetResponse(GetResponse response) {
        try {
            channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void nackGetResponse(GetResponse response) {
        try {
            channel.basicNack(response.getEnvelope().getDeliveryTag(), false, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends Serializable> Optional<List<T>> take(EntityMatcher<T> matcher, int count) {
        Preconditions.checkState(txManager.isTransactionActive(), "take requires active transaction");

        log.info("Taking {} entities matching {}", count, matcher);

        List<T> entities = new ArrayList<>();

        for (int i = 0; i < count; ++i) {
            Optional<T> entity = takeOne(matcher);

            if (!entity.isPresent()) {
                return Optional.empty();
            }

            entities.add(entity.get());
        }

        return Optional.of(entities);
    }

    @Override
    public int count(EntityMatcher<? extends Serializable> matcher, int nMaxEntities) {
        Preconditions.checkState(!txManager.isTransactionActive(), "Count should not run in transaction!");
        int count = 0;

        for (GetResponse response = getNextPotentialMatching(matcher);
             response != null && count < nMaxEntities;
             response = getNextPotentialMatching(matcher)) {
            nackGetResponse(response);

            Serializable entity = (Serializable) deserialize(response.getBody());

            if (matcher.matches(entity)) {
                count++;
            }
        }

        txManager.commit();

        log.info("Counted at least {} entities matching {}", count, matcher);

        return count;
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
                Event event = (Event) deserialize(body);

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

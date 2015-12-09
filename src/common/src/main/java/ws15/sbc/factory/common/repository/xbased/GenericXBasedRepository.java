package ws15.sbc.factory.common.repository.xbased;

import com.rabbitmq.client.*;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.Repository;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Collections.synchronizedList;
import static ws15.sbc.factory.common.repository.xbased.Event.ActionType.STORED;
import static ws15.sbc.factory.common.repository.xbased.Event.ActionType.TAKEN;

public abstract class GenericXBasedRepository<Entity extends Serializable> implements Repository<Entity> {

    private static final Logger log = LoggerFactory.getLogger(GenericXBasedRepository.class);

    private String exchange;
    private String eventsRoutingKey;

    private Optional<String> eventQueueName = Optional.empty();
    private List<Consumer<Entity>> storedEventConsumers = synchronizedList(new ArrayList<>());
    private List<Consumer<Entity>> takenEventConsumers = synchronizedList(new ArrayList<>());

    private Channel channel;

    public GenericXBasedRepository(Channel channel, String repoName) {
        this.channel = channel;

        exchange = repoName + "-exchange";
        eventsRoutingKey = repoName + "-events";

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
        getQueueTypes().stream()
                .map(Class::getSimpleName)
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

    public abstract List<Class<? extends Entity>> getQueueTypes();

    @Override
    public void storeEntity(Entity entity) {
        log.info("Publishing entity {} to exchange {}", entity, exchange);

        String routingKey = entity.getClass().getSimpleName();
        byte[] content = SerializationUtils.serialize(entity);

        publish(content, routingKey);
        notifyAction(entity, STORED);
    }

    @Override
    public void storeEntities(List<? extends Entity> entities) {
        entities.forEach(this::storeEntity);
    }

    @Override
    public <T extends Entity> Optional<T> takeOne(Class<T> clazz) {
        log.info("Getting entity of type {}", clazz);

        Optional<GetResponse> response = getFirstMatchingOfType(clazz);
        if (!response.isPresent()) {
            log.info("Empty response for type {}", clazz);
            return Optional.empty();
        }

        Object entity = SerializationUtils.deserialize(response.get().getBody());
        log.info("Got entity {}", entity);

        notifyAction((Entity) entity, TAKEN);

        return Optional.of((T) entity);
    }

    private Optional<GetResponse> getFirstMatchingOfType(Class<?> clazz) {
        Optional<GetResponse> response = Optional.empty();

        for (Class potentialType : getCompatibleEntityTypesFor(clazz)) {
            response = getFromQueueAndAckDelivery(potentialType.getSimpleName());

            if (response.isPresent()) break;
        }

        return response;
    }

    private List<Class<? extends Entity>> getCompatibleEntityTypesFor(Class<?> clazz) {
        return getQueueTypes().stream()
                .filter(clazz::isAssignableFrom)
                .collect(Collectors.toList());
    }

    private Optional<GetResponse> getFromQueueAndAckDelivery(String queueName) {
        log.info("Trying to get entity from queue {}", queueName);

        try {
            GetResponse response = channel.basicGet(queueName, false);
            if (response == null) {
                return Optional.empty();
            }

            channel.basicAck(response.getEnvelope().getDeliveryTag(), false);

            return Optional.of(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void notifyAction(Entity entity, Event.ActionType actionType) {
        byte[] eventContent = SerializationUtils.serialize(new Event<>(entity, actionType));
        publish(eventContent, eventsRoutingKey);
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

    @Override
    public void onEntityStored(Consumer<Entity> consumer) {
        log.info("Registering listener for entity stored events");

        storedEventConsumers.add(consumer);

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
                Event<Entity> event = (Event<Entity>) SerializationUtils.deserialize(body);

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
    public void onEntityTaken(Consumer<Entity> consumer) {
        log.info("Registering listener for entity taken events");

        takenEventConsumers.add(consumer);

        startEventQueueIfNotActive();
    }
}

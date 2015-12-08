package ws15.sbc.factory.common.repository.xbased;

import com.rabbitmq.client.*;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.Repository;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class GenericXBasedRepository<Entity extends Serializable> implements Repository<Entity> {

    private static final Logger log = LoggerFactory.getLogger(GenericXBasedRepository.class);

    private String exchange;
    private String eventsRoutingKey;

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
        getQueuesType().stream()
                .map(Class::getSimpleName)
                .forEach(it -> {
                    log.info("Declaring queue {} bound to exchange {}", it, exchange);

                    try {
                        channel.queueDeclare(it, false, false, true, null);

                        channel.queueBind(it, exchange, it);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public abstract List<Class<? extends Entity>> getQueuesType();

    @Override
    public void storeEntity(Entity entity) {
        log.info("Publishing entity {} to exchange {}", entity, exchange);

        String routingKey = entity.getClass().getSimpleName();
        byte[] content = SerializationUtils.serialize(entity);

        publish(content, routingKey);
        notifyAction(entity, Event.ActionType.STORED);
    }

    @Override
    public void storeEntities(List<? extends Entity> entities) {
        entities.forEach(this::storeEntity);
    }

    @Override
    public <T extends Entity> Optional<T> takeOne(Class<T> clazz) {
        log.info("Getting entity from queue {}", clazz.getSimpleName());

        Optional<GetResponse> response = getFromQueueAndAckDelivery(clazz.getSimpleName());
        if (!response.isPresent()) {
            log.info("Empty response from queue {}", clazz.getSimpleName());
            return Optional.empty();
        }

        Object entity = SerializationUtils.deserialize(response.get().getBody());
        log.info("Got entity {} from queue {}", entity, clazz.getSimpleName());

        notifyAction((Entity) entity, Event.ActionType.TAKEN);

        return Optional.of((T) entity);
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

    private Optional<GetResponse> getFromQueueAndAckDelivery(String queueName) {
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

    @Override
    public void onEntityStored(Consumer<Entity> consumer) {
        log.info("Registering listener for entity stored events");

        setConsumerForTopic(consumer, Event.ActionType.STORED);
    }

    @Override
    public void onEntityTaken(Consumer<Entity> consumer) {
        log.info("Registering listener for entity taken events");

        setConsumerForTopic(consumer, Event.ActionType.TAKEN);
    }

    private void setConsumerForTopic(Consumer<Entity> consumer, Event.ActionType actionType) {
        try {
            channel.basicConsume(prepareEventQueueFor(), prepareQueueConsumer(consumer, actionType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String prepareEventQueueFor() {
        log.info("Preparing event queue for routing key {}", eventsRoutingKey);

        try {
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchange, eventsRoutingKey);

            return queueName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private com.rabbitmq.client.Consumer prepareQueueConsumer(final Consumer<Entity> consumer, Event.ActionType actionType) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Event<Entity> event = (Event<Entity>) SerializationUtils.deserialize(body);

                if (event.getActionType() == actionType) {
                    log.info("Queue consumer woken up for {} with action {}", event.getEntity(), actionType);
                    consumer.accept(event.getEntity());
                }
            }
        };
    }

}

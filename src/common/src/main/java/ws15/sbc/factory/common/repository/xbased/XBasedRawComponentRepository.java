package ws15.sbc.factory.common.repository.xbased;

import com.rabbitmq.client.*;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.*;
import ws15.sbc.factory.common.repository.RawComponentRepository;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

public class XBasedRawComponentRepository implements RawComponentRepository {

    private static final Logger log = LoggerFactory.getLogger(XBasedRawComponentRepository.class);

    private static final String RAW_COMPONENTS_EXCHANGE = "raw-components-exchange";
    private static final String STORED_EVENTS = "raw-component-stored";
    private static final String TAKEN_EVENTS = "raw-component-taken";

    private Channel channel;

    @Inject
    public XBasedRawComponentRepository(Channel channel) {
        this.channel = channel;

        try {
            initializeExchange();

            initializeRawComponentQueues();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeExchange() throws IOException {
        log.info("Declaring exchange {}", RAW_COMPONENTS_EXCHANGE);

        channel.exchangeDeclare(RAW_COMPONENTS_EXCHANGE, "topic");
    }

    private void initializeRawComponentQueues() {
        asList(Engine.class, Rotor.class, Casing.class, ControlUnit.class).stream()
                .map(Class::getSimpleName)
                .forEach(it -> {
                    log.info("Declaring queue {} bound to exchange {}", it, RAW_COMPONENTS_EXCHANGE);

                    try {
                        channel.queueDeclare(it, false, false, true, null);

                        channel.queueBind(it, RAW_COMPONENTS_EXCHANGE, it);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void storeEntity(RawComponent rawComponent) {
        log.info("Publishing entity {} to exchange {}", rawComponent, RAW_COMPONENTS_EXCHANGE);

        String routingKey = rawComponent.getClass().getSimpleName();
        byte[] content = SerializationUtils.serialize(rawComponent);

        publish(content, routingKey, STORED_EVENTS);
    }

    private void publish(byte[] content, String... routingKeys) {
        try {
            for (String routingKey : routingKeys) {
                channel.basicPublish(RAW_COMPONENTS_EXCHANGE, routingKey, null, content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void storeEntities(List<? extends RawComponent> rawComponents) {
        rawComponents.forEach(this::storeEntity);
    }

    @Override
    public <T extends RawComponent> Optional<T> takeOne(Class<T> clazz) {
        log.info("Getting entity from queue {}", clazz.getSimpleName());

        Optional<GetResponse> response = getFromQueueAndAckDelivery(clazz.getSimpleName());
        if (!response.isPresent()) {
            log.info("Empty response from queue {}", clazz.getSimpleName());
            return Optional.empty();
        }

        notifyComponentTaken(response.get().getBody());

        Object component = SerializationUtils.deserialize(response.get().getBody());
        log.info("Got component {} from queue {}", component, clazz.getSimpleName());


        return Optional.of((T) component);
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

    private void notifyComponentTaken(byte[] componentContent) {
        publish(componentContent, TAKEN_EVENTS);
    }

    @Override
    public List<RawComponent> readAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEntityStored(Consumer<RawComponent> consumer) {
        log.info("Registering listener for raw component stored events");

        prepareConsumerForTopic(consumer, STORED_EVENTS);
    }

    private void prepareConsumerForTopic(Consumer<RawComponent> consumer, String topic) {
        try {
            channel.basicConsume(prepareEventQueueFor(topic), prepareQueueConsumer(consumer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String prepareEventQueueFor(String routingKey) {
        log.info("Preparing event queue for routing key {}", routingKey);

        try {
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, RAW_COMPONENTS_EXCHANGE, STORED_EVENTS);

            return queueName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private com.rabbitmq.client.Consumer prepareQueueConsumer(final Consumer<RawComponent> consumer) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.info("Queue consumer woken up");

                consumer.accept((RawComponent) SerializationUtils.deserialize(body));
            }
        };
    }

    @Override
    public void onEntityTaken(Consumer<RawComponent> consumer) {
        log.info("Registering listener for raw component taken events");

        prepareConsumerForTopic(consumer, TAKEN_EVENTS);
    }

//    public static void main(String[] argv) {
//        Injector injector = Guice.createInjector(new CommonModule());
//
//        RawComponentRepository repo = injector.getInstance(RawComponentRepository.class);
//
//        repo.onEntityStored(rawComponent -> log.info("Stored: {}", rawComponent));
//        repo.onEntityTaken(rawComponent -> log.info("Taken: {}", rawComponent));
//
//        repo.storeEntity(new Engine("123"));
//        log.info("Got one: {}", repo.takeOne(Engine.class).get());
//
//        injector.getInstance(AppManager.class).shutdown();
//    }
}

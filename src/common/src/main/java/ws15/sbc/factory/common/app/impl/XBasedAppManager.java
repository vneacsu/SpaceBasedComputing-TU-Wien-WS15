package ws15.sbc.factory.common.app.impl;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.app.AppManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Singleton
public class XBasedAppManager implements AppManager {

    private static final Logger log = LoggerFactory.getLogger(XBasedAppManager.class);

    @Inject
    private Channel channel;
    @Inject
    private Connection connection;

    private Optional<HazelcastInstance> hazelcastServer = Optional.empty();

    @Override
    public void prepareInfrastructure() {
        hazelcastServer = Optional.of(Hazelcast.newHazelcastInstance());
    }

    @Override
    public void shutdown() {
        log.info("Shutting application down");

        closeChannel();
        closeConnection();
        closeHazelcast();

        log.info("Application successfully shut down");
    }

    private void closeChannel() {
        try {
            channel.close();
        } catch (IOException | TimeoutException e) {
            log.error("Failed to close connection", e);
        }
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (IOException e) {
            log.error("Failed to close connection", e);
        }
    }

    private void closeHazelcast() {
        hazelcastServer.ifPresent(HazelcastInstance::shutdown);
    }
}

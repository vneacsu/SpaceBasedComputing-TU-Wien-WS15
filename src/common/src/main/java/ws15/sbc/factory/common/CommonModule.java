package ws15.sbc.factory.common;

import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.config.CommonsXmlConfiguration;
import org.mozartspaces.core.config.Configuration;
import org.mozartspaces.notifications.NotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.app.AppManager;
import ws15.sbc.factory.common.app.impl.SpaceBasedAppManager;
import ws15.sbc.factory.common.app.impl.XBasedAppManager;
import ws15.sbc.factory.common.repository.Repository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.common.repository.mzs.SpaceBasedRepository;
import ws15.sbc.factory.common.repository.mzs.SpaceBasedTxManager;
import ws15.sbc.factory.common.repository.xBased.XBasedRepository;
import ws15.sbc.factory.common.repository.xBased.XBasedTxManager;
import ws15.sbc.factory.common.utils.PropertyUtils;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

public class CommonModule extends PrivateModule {

    private static final Logger log = LoggerFactory.getLogger(PrivateModule.class);

    @Override
    protected void configure() {
        expose(AppManager.class);
        expose(Repository.class);
        expose(TxManager.class);

        final String repoStrategy = PropertyUtils.getProperty("repoStrategy").orElse("spaceBased");
        log.info("Setting repository strategy to: {}", repoStrategy);

        switch (repoStrategy) {
            case "spaceBased":
                bindSpaceBased();
                break;
            case "xBased":
                bindXBased();
                break;
            default:
                throw new IllegalArgumentException("Invalid repoStrategy");
        }
    }

    private void bindSpaceBased() {
        Configuration config = CommonsXmlConfiguration.loadFrom(getClass().getResource("/mozartspaces-client.xml"));
        MzsCore core = DefaultMzsCore.newInstance(config);

        bind(MzsCore.class).toInstance(core);
        bind(Capi.class).toInstance(new Capi(core));
        bind(NotificationManager.class).toInstance(new NotificationManager(core));

        final int spacePortOffset = Integer.valueOf(PropertyUtils.getProperty("factoryNo").orElse("0"));
        final int spacePort = SpaceBasedAppManager.SPACE_PORT + spacePortOffset;
        bind(URI.class).toInstance(URI.create("xvsm://localhost:" + spacePort));

        bind(AppManager.class).to(SpaceBasedAppManager.class);
        bind(Repository.class).to(SpaceBasedRepository.class);
        bind(TxManager.class).to(SpaceBasedTxManager.class);
    }

    private void bindXBased() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try {
            Connection connection = connectionFactory.newConnection();

            bind(Connection.class).toInstance(connection);
            bind(Channel.class).toInstance(connection.createChannel());
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        bind(AppManager.class).to(XBasedAppManager.class);
        bind(Repository.class).to(XBasedRepository.class);
        bind(TxManager.class).to(XBasedTxManager.class);

        System.setProperty("hazelcast.logging.type", "none");

        bind(HazelcastInstance.class).toProvider(() -> HazelcastClient.newHazelcastClient()).in(Singleton.class);
    }
}

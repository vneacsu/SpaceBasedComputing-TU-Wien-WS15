package ws15.sbc.factory.common.app.impl;

import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.config.CommonsXmlConfiguration;
import org.mozartspaces.core.config.Configuration;
import org.mozartspaces.notifications.NotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.app.AppManager;
import ws15.sbc.factory.common.utils.PropertyUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URL;
import java.util.Optional;

@Singleton
public class SpaceBasedAppManager implements AppManager {

    private static final Logger log = LoggerFactory.getLogger(SpaceBasedAppManager.class);

    public static final int SPACE_PORT = 4242;

    @Inject
    private MzsCore core;
    @Inject
    private NotificationManager notificationManager;

    private Optional<MzsCore> spaceCore = Optional.empty();

    @Override
    public void prepareInfrastructure() {
        final int spacePortOffset = Integer.valueOf(PropertyUtils.getProperty("factoryNo").orElse("0"));
        final int spacePort = SPACE_PORT + spacePortOffset;

        log.info("Starting space server on port {}", spacePort);
        spaceCore = Optional.of(DefaultMzsCore.newInstance(spacePort));
    }

    @Override
    public void shutdown() {
        log.info("Shutting application down...");

        notificationManager.shutdown();
        core.shutdown(true);

        spaceCore.ifPresent(it -> it.shutdown(true));

        log.info("Application successfully shut down");
    }
}

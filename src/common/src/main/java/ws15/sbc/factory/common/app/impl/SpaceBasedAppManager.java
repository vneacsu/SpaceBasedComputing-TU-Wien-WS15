package ws15.sbc.factory.common.app.impl;

import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.notifications.NotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.app.AppManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class SpaceBasedAppManager implements AppManager {

    private static final Logger log = LoggerFactory.getLogger(SpaceBasedAppManager.class);

    private static final int SPACE_PORT = 4242;

    @Inject
    private MzsCore core;
    @Inject
    private NotificationManager notificationManager;

    private Optional<MzsCore> spaceCore = Optional.empty();

    @Override
    public void prepareInfrastructure() {
        log.info("Starting space server on port {}", SPACE_PORT);

        spaceCore = Optional.of(DefaultMzsCore.newInstance(SPACE_PORT));
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

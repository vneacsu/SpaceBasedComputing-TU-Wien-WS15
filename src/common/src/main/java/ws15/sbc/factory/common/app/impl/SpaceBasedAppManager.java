package ws15.sbc.factory.common.app.impl;

import org.mozartspaces.core.MzsCore;
import org.mozartspaces.notifications.NotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.app.AppManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SpaceBasedAppManager implements AppManager {

    private static final Logger log = LoggerFactory.getLogger(SpaceBasedAppManager.class);

    @Inject
    private MzsCore core;
    @Inject
    private NotificationManager notificationManager;

    @Override
    public void shutdown() {
        log.info("Shutting application down...");

        notificationManager.shutdown();
        core.shutdown(true);

        log.info("Application successfully shut down");
    }
}

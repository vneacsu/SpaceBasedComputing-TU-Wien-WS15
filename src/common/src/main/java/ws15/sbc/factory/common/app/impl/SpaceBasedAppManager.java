package ws15.sbc.factory.common.app.impl;

import org.mozartspaces.core.MzsCore;
import ws15.sbc.factory.common.app.AppManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SpaceBasedAppManager implements AppManager {

    @Inject
    private MzsCore core;

    @Override
    public void shutdown() {
        core.shutdown(true);
    }
}

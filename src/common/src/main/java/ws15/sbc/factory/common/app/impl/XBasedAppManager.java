package ws15.sbc.factory.common.app.impl;

import ws15.sbc.factory.common.app.AppManager;

import javax.inject.Singleton;

@Singleton
public class XBasedAppManager implements AppManager {

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }
}

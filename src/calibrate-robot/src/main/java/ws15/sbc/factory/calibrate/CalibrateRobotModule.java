package ws15.sbc.factory.calibrate;

import com.google.inject.PrivateModule;

public class CalibrateRobotModule extends PrivateModule {

    @Override
    protected void configure() {
        expose(CalibrateRobot.class);
        bind(CalibrateRobot.class);
    }

}

package ws15.sbc.factory.calibrate;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ws15.sbc.factory.common.CommonModule;
import ws15.sbc.factory.common.app.AppManager;

public class Main {

    private final static Injector injector = Guice.createInjector(new CommonModule(), new CalibrateRobotModule());
    private final static CalibrateRobot robot = injector.getInstance(CalibrateRobot.class);
    private static final AppManager appManager = injector.getInstance(AppManager.class);

    public static void main(String[] argv) {
        appManager.registerShutdownHook(robot::stop);

        robot.run();
    }
}

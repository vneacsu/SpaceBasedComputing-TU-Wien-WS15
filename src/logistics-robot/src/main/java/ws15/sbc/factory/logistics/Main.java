package ws15.sbc.factory.logistics;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ws15.sbc.factory.common.CommonModule;
import ws15.sbc.factory.common.app.AppManager;
import ws15.sbc.factory.common.utils.PropertyUtils;

public class Main {

    private static final Integer admissibleRange = Integer.parseInt(PropertyUtils.getProperty("admissibleRange").orElse("" + 15));

    private static final Injector injector = Guice.createInjector(new CommonModule(), new LogisticsRobotModule(admissibleRange));
    private static final LogisticsRobot robot = injector.getInstance(LogisticsRobot.class);
    private static final AppManager appManager = injector.getInstance(AppManager.class);

    public static void main(String[] argv) {
        registerShutdownHook();

        robot.run();
    }

    private static void registerShutdownHook() {
        Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    robot.stop();
                    try {
                        mainThread.join();
                    } catch (InterruptedException ignore) {
                    }

                    appManager.shutdown();
                })
        );
    }
}

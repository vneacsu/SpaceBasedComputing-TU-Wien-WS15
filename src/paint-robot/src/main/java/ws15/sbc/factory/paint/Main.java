package ws15.sbc.factory.paint;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.mozartspaces.core.MzsCoreException;
import ws15.sbc.factory.common.CommonModule;
import ws15.sbc.factory.common.app.AppManager;
import ws15.sbc.factory.common.utils.PropertyUtils;

import java.util.UUID;

public class Main {

    private static final String robotId = PropertyUtils.getProperty("robotId").orElse(UUID.randomUUID().toString());

    private static final Injector injector = Guice.createInjector(
            new CommonModule(),
            new PaintRobotModule(robotId)
    );

    private static final PaintRobot paintRobot = injector.getInstance(PaintRobot.class);

    private static final AppManager appManager = injector.getInstance(AppManager.class);

    public static void main(String[] argv) throws MzsCoreException {
        appManager.registerShutdownHook(paintRobot::stop);

        paintRobot.run();
    }


}

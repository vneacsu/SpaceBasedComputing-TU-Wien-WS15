package ws15.sbc.factory.assembly;

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
            new AssemblyRobotModule(robotId)
    );

    private static final AssemblyRobot assemblyRobot = injector.getInstance(AssemblyRobot.class);

    private static final AppManager appManager = injector.getInstance(AppManager.class);

    public static void main(String[] argv) throws MzsCoreException {
        appManager.registerShutdownHook(assemblyRobot::stop);

        assemblyRobot.run();
    }
}

package ws15.sbc.factory.assembly;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.mozartspaces.core.MzsCoreException;
import ws15.sbc.factory.common.CommonModule;
import ws15.sbc.factory.common.app.AppManager;

public class Main {

    public static void main(String[] argv) throws MzsCoreException {
        String id = "dummy";

        Injector injector = Guice.createInjector(
                new CommonModule(),
                new AssemblyRobotModule(id)
        );

        injector.getInstance(AssemblyRobot.class).run();

        injector.getInstance(AppManager.class).shutdown();
    }

}

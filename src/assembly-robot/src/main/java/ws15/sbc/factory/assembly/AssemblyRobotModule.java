package ws15.sbc.factory.assembly;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import java.util.List;

import static java.util.Collections.singletonList;

class AssemblyRobotModule extends PrivateModule {

    private final String robotId;

    AssemblyRobotModule(String robotId) {
        this.robotId = robotId;
    }

    @Override
    protected void configure() {
        expose(AssemblyRobot.class);

        bind(AssemblyRobot.class);

        bind(String.class).annotatedWith(Names.named("RobotId")).toInstance(robotId);
    }

    @Provides
    public List<AssemblyRobotStep> provideAssemblyRobotSteps(AssemblyRobot assemblyRobot) {
        return singletonList(new EngineRotorPairAssemblyStep(assemblyRobot));
    }
}

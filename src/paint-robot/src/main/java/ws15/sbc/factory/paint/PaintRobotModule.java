package ws15.sbc.factory.paint;

import com.google.inject.PrivateModule;
import com.google.inject.name.Names;

public class PaintRobotModule extends PrivateModule {

    private final String robotId;

    public PaintRobotModule(String robotId) {
        this.robotId = robotId;
    }

    @Override
    protected void configure() {
        expose(PaintRobot.class);
        bind(PaintRobot.class);
        bind(String.class).annotatedWith(Names.named("RobotId")).toInstance(robotId);
    }
}

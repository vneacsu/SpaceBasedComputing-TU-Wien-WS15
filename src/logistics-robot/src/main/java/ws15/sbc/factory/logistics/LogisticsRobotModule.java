package ws15.sbc.factory.logistics;

import com.google.inject.PrivateModule;
import com.google.inject.name.Names;

public class LogisticsRobotModule extends PrivateModule {

    private final Integer admissibleRange;

    public LogisticsRobotModule(Integer admissibleRange) {
        this.admissibleRange = admissibleRange;
    }

    @Override
    protected void configure() {
        expose(LogisticsRobot.class);
        bind(LogisticsRobot.class);

        bind(Integer.class).annotatedWith(Names.named("AdmissibleRange")).toInstance(admissibleRange);
    }

}

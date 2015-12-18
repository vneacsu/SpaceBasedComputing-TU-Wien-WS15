package ws15.sbc.factory.logistics;

import com.google.inject.PrivateModule;
import com.google.inject.name.Names;
import ws15.sbc.factory.common.utils.PropertyUtils;

import java.util.UUID;

public class LogisticsRobotModule extends PrivateModule {

    private static final String robotId = PropertyUtils.getProperty("robotId").orElse(UUID.randomUUID().toString());

    private final Integer admissibleRange;

    public LogisticsRobotModule(Integer admissibleRange) {
        this.admissibleRange = admissibleRange;
    }

    @Override
    protected void configure() {
        expose(LogisticsRobot.class);
        bind(LogisticsRobot.class);

        bind(Integer.class).annotatedWith(Names.named("AdmissibleRange")).toInstance(admissibleRange);
        bind(String.class).annotatedWith(Names.named("robotId")).toInstance(robotId);
    }

}

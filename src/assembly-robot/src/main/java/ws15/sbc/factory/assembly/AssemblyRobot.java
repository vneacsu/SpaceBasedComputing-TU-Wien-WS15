package ws15.sbc.factory.assembly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.assembly.steps.AssemblyStep;
import ws15.sbc.factory.assembly.steps.CarcaseAssemblyStep;
import ws15.sbc.factory.assembly.steps.DroneAsemblyStep;
import ws15.sbc.factory.assembly.steps.EngineRotorPairAssemblyStep;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Arrays.asList;

@Singleton
public class AssemblyRobot {
    private static final Logger log = LoggerFactory.getLogger(AssemblyRobot.class);

    @Inject
    @Named("RobotId")
    private String robotId;

    @Inject
    private EngineRotorPairAssemblyStep engineRotorPairAssemblyStep;

    @Inject
    private CarcaseAssemblyStep carcaseAssemblyStep;

    @Inject
    private DroneAsemblyStep droneAsemblyStep;

    private final AtomicBoolean keepWorking = new AtomicBoolean(true);

    public void run() {
        log.info("Starting assembly robot <{}> started work", robotId);

        while (keepWorking.get()) {
            try {
                asList(engineRotorPairAssemblyStep, carcaseAssemblyStep, droneAsemblyStep)
                        .forEach(AssemblyStep::performStep);
            } catch (Exception e) {
                log.error("Assembly exception caught", e);
            }
        }

        log.info("Assembly robot <{}> finished work", robotId);
    }

    public void stop() {
        log.info("Stopping assembly robot {}", robotId);

        keepWorking.set(false);
    }
}

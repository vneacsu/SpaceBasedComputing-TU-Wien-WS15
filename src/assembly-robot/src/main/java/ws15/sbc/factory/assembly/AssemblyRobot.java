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

    public void run() {
        log.info("Starting assembly robot <{}> started work", robotId);

        asList(engineRotorPairAssemblyStep, carcaseAssemblyStep, droneAsemblyStep)
                .forEach(AssemblyStep::performStep);

        log.info("Assembly robot <{}> finished work", robotId);
    }
}

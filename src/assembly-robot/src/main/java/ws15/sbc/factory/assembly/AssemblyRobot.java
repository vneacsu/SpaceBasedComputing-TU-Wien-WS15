package ws15.sbc.factory.assembly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class AssemblyRobot {
    private static final Logger log = LoggerFactory.getLogger(AssemblyRobot.class);

    @Inject
    @Named("RobotId")
    private String robotId;

    @Inject
    private EngineRotorPairAssemblyStep engineRotorPairAssemblyStep;

    public void run() {
        log.info("Starting assembly robot <{}> started work", robotId);

        engineRotorPairAssemblyStep.performStep();

        log.info("Assembly robot <{}> finished work", robotId);
    }
}

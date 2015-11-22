package ws15.sbc.factory.assembly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.ComponentRepository;

import java.util.ArrayList;
import java.util.List;

public class AssemblyRobot {
    private static final Logger log = LoggerFactory.getLogger(AssemblyRobot.class);

    private final String robotId;
    private final ComponentRepository componentRepository;

    private final AssemblyRobotLocalStorage assemblyRobotLocalStorage;

    private final List<AssemblyRobotStep> steps = new ArrayList<>();

    public AssemblyRobot(String robotId, ComponentRepository componentRepository) {
        this.robotId = robotId;
        this.componentRepository = componentRepository;

        this.assemblyRobotLocalStorage = new AssemblyRobotLocalStorage();

        steps.add(new EngineRotorPairAssemblyStep(this));
    }

    public String getRobotId() {
        return robotId;
    }

    public ComponentRepository getComponentRepository() {
        return componentRepository;
    }

    public AssemblyRobotLocalStorage getAssemblyRobotLocalStorage() {
        return assemblyRobotLocalStorage;
    }

    public void run() {
        log.info("Starting assembly robot <{}> started work", robotId);

        steps.forEach(AssemblyRobotStep::performStep);

        log.info("Assembly robot <{}> finished work", robotId);
    }
}

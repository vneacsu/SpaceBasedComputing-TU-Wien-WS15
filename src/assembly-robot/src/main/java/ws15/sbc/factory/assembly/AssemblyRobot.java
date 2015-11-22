package ws15.sbc.factory.assembly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.ComponentRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class AssemblyRobot {
    private static final Logger log = LoggerFactory.getLogger(AssemblyRobot.class);

    @Inject
    @Named("RobotId")
    private String robotId;
    @Inject
    private ComponentRepository componentRepository;

    @Inject
    private AssemblyRobotLocalStorage assemblyRobotLocalStorage;

    @Inject
    private List<AssemblyRobotStep> steps;

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

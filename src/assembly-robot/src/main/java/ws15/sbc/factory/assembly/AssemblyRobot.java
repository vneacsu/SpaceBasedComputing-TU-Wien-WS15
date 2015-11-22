package ws15.sbc.factory.assembly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.common.repository.TxManager;

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
    private RawComponentRepository rawComponentRepository;

    @Inject
    private AssemblyRobotLocalStorage assemblyRobotLocalStorage;

    @Inject
    private TxManager txManager;

    @Inject
    private List<AssemblyRobotStep> steps;

    public String getRobotId() {
        return robotId;
    }

    public RawComponentRepository getRawComponentRepository() {
        return rawComponentRepository;
    }

    public AssemblyRobotLocalStorage getAssemblyRobotLocalStorage() {
        return assemblyRobotLocalStorage;
    }

    public TxManager getTxManager() {
        return txManager;
    }

    public void run() {
        log.info("Starting assembly robot <{}> started work", robotId);

        steps.forEach(AssemblyRobotStep::performStep);

        log.info("Assembly robot <{}> finished work", robotId);
    }
}

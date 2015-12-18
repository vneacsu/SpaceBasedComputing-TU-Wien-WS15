package ws15.sbc.factory.assembly.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.assembly.AssemblyRobotLocalStorage;
import ws15.sbc.factory.common.dto.Carcase;
import ws15.sbc.factory.common.dto.Casing;
import ws15.sbc.factory.common.dto.ControlUnit;
import ws15.sbc.factory.common.repository.EntityMatcher;
import ws15.sbc.factory.common.repository.Repository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.common.utils.OperationUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class CarcaseAssemblyStep implements AssemblyStep {

    private static final Logger log = LoggerFactory.getLogger(CarcaseAssemblyStep.class);

    @Inject
    @Named("RobotId")
    private String robotId;
    @Inject
    private TxManager txManager;
    @Inject
    private Repository repository;
    @Inject
    private AssemblyRobotLocalStorage assemblyRobotLocalStorage;

    @Override
    public void performStep() {
        log.info("Performing carcase assembly step");

        txManager.beginTransaction();

        Optional<Carcase> carcase = acquireOrAssembleCarcase();

        if (carcase.isPresent()) {
            log.info("Carcase successfully acquired/assembled");
            assemblyRobotLocalStorage.storeCarcase(carcase.get());

            txManager.commit();
        } else {
            log.info("Carcase could have not been acquired/assembled");

            txManager.rollback();
        }
    }

    private Optional<Carcase> acquireOrAssembleCarcase() {
        Optional<Carcase> carcase = acquireCarcaseFromInventory();

        if (!carcase.isPresent()) {
            log.info("No carcase found in inventory");
            carcase = assembleNewCarcase();
        }

        return carcase;
    }

    private Optional<Carcase> acquireCarcaseFromInventory() {
        log.info("Trying to acquire carcase from inventory");

        return repository.takeOne(EntityMatcher.of(Carcase.class));
    }

    private Optional<Carcase> assembleNewCarcase() {
        log.info("Trying to assemble new carcase");

        Optional<Casing> casing = repository.takeOne(EntityMatcher.of(Casing.class));
        if (!casing.isPresent()) {
            return Optional.empty();
        }

        Optional<ControlUnit> controlUnit = repository.takeOne(EntityMatcher.of(ControlUnit.class));
        if (!controlUnit.isPresent()) {
            return Optional.empty();
        }

        OperationUtils.simulateDelay(1000);

        return Optional.of(new Carcase(robotId, casing.get(), controlUnit.get()));
    }
}

package ws15.sbc.factory.assembly.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.Carcase;
import ws15.sbc.factory.common.dto.Drone;
import ws15.sbc.factory.common.dto.EngineRotorPair;
import ws15.sbc.factory.common.repository.EntityMatcher;
import ws15.sbc.factory.common.repository.Repository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.common.utils.OperationUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class DroneAsemblyStep implements AssemblyStep {

    private static final Logger log = LoggerFactory.getLogger(DroneAsemblyStep.class);

    private static final int N_REQUIRED_ENGINE_ROTOR_PAIRS = 3;

    @Inject
    @Named("RobotId")
    private String robotId;

    @Inject
    private Repository repository;
    @Inject
    private TxManager txManager;

    private Optional<List<EngineRotorPair>> availableEngineRotorPairs = Optional.empty();
    private Optional<Carcase> availableCarcase = Optional.empty();

    @Override
    public void performStep() {
        log.info("Performing drone assembly step");

        txManager.beginTransaction();

        acquireComponentsFromInventory();

        if (itCanAssembleDrone()) {
            assembleDroneAndStoreItInInventory();
            txManager.commit();
        } else {
            log.info("Insufficient resources to assemble a complete drone");
            txManager.rollback();
        }

    }

    private void acquireComponentsFromInventory() {
        availableEngineRotorPairs = repository.take(EntityMatcher.of(EngineRotorPair.class), N_REQUIRED_ENGINE_ROTOR_PAIRS);
        availableCarcase = repository.takeOne(EntityMatcher.of(Carcase.class));
    }

    private boolean itCanAssembleDrone() {
        return availableEngineRotorPairs.isPresent() && availableCarcase.isPresent();
    }

    private void assembleDroneAndStoreItInInventory() {
        log.info("Assembling drone and storing it in inventory");

        Drone drone = new Drone(robotId, availableEngineRotorPairs.get(), availableCarcase.get());
        OperationUtils.simulateDelay(1000);

        repository.storeEntity(drone);
    }
}

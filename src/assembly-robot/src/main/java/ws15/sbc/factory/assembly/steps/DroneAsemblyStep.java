package ws15.sbc.factory.assembly.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.assembly.AssemblyRobotLocalStorage;
import ws15.sbc.factory.common.dto.Carcase;
import ws15.sbc.factory.common.dto.Drone;
import ws15.sbc.factory.common.dto.EngineRotorPair;
import ws15.sbc.factory.common.repository.Repository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.common.utils.OperationUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Singleton
public class DroneAsemblyStep implements AssemblyStep {

    private static final Logger log = LoggerFactory.getLogger(DroneAsemblyStep.class);

    private static final int N_REQUIRED_ENGINE_ROTOR_PAIRS = 3;

    @Inject
    @Named("RobotId")
    private String robotId;

    @Inject
    private AssemblyRobotLocalStorage assemblyRobotLocalStorage;
    @Inject
    private Repository repository;
    @Inject
    private TxManager txManager;

    private List<EngineRotorPair> availableEngineRotorPairs = emptyList();
    private Optional<Carcase> availableCarcase = Optional.empty();

    @Override
    public void performStep() {
        log.info("Performing drone assembly step");

        acquireComponentsFromLocalStorage();

        txManager.beginTransaction();

        if (itCanAssembleDrone()) {
            assembleDroneAndStoreItInInventory();
        } else {
            log.info("Insufficient resources to assemble a complete drone");
            storeAvailableComponentsInInventoryForFutureUse();
        }

        txManager.commit();
    }

    private void acquireComponentsFromLocalStorage() {
        availableEngineRotorPairs = assemblyRobotLocalStorage.consumeEngineRotorPairs();
        availableCarcase = assemblyRobotLocalStorage.consumeCarcase();
    }

    private boolean itCanAssembleDrone() {
        return availableEngineRotorPairs.size() == N_REQUIRED_ENGINE_ROTOR_PAIRS &&
                availableCarcase.isPresent();
    }

    private void assembleDroneAndStoreItInInventory() {
        log.info("Assembling drone and storing it in inventory");

        Drone drone = new Drone(robotId, availableEngineRotorPairs, availableCarcase.get());
        OperationUtils.simulateDelay(1000);

        clearAvailableComponents();

        repository.storeEntity(drone);
    }

    private void clearAvailableComponents() {
        availableEngineRotorPairs = emptyList();
        availableCarcase = Optional.empty();
    }

    private void storeAvailableComponentsInInventoryForFutureUse() {
        log.info("Storing available engine rotor pairs and carcase in inventory, for future use");

        if (availableEngineRotorPairs.size() > 0) {
            repository.storeEntities(availableEngineRotorPairs);
        }

        availableCarcase.ifPresent(carcase -> repository.storeEntity(carcase));

        clearAvailableComponents();
    }
}

package ws15.sbc.factory.assembly.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.assembly.AssemblyRobotLocalStorage;
import ws15.sbc.factory.common.repository.EntitySpecification;
import ws15.sbc.factory.common.repository.ProcessedComponentRepository;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.dto.Carcase;
import ws15.sbc.factory.dto.Casing;
import ws15.sbc.factory.dto.ControlUnit;
import ws15.sbc.factory.dto.RawComponent;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class CarcaseAssemblyStep extends TransactionalAssemblyStep {

    private static final Logger log = LoggerFactory.getLogger(CarcaseAssemblyStep.class);

    @Inject
    @Named("RobotId")
    private String robotId;
    @Inject
    private ProcessedComponentRepository processedComponentRepository;
    @Inject
    private RawComponentRepository rawComponentRepository;
    @Inject
    private AssemblyRobotLocalStorage assemblyRobotLocalStorage;

    @Override
    protected void performStepWithinTransaction() {
        log.info("Performing carcase assembly step");

        Optional<Carcase> carcase = acquireOrAssembleCarcase();

        if (carcase.isPresent()) {
            log.info("Carcase successfully acquired/assembled");
            assemblyRobotLocalStorage.storeCarcase(carcase.get());
        } else {
            log.info("Carcase could have not been acquired/assembled");
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

        List<Carcase> carcases = processedComponentRepository.takeComponents(new EntitySpecification(Carcase.class));

        return carcases.isEmpty() ? Optional.empty() : Optional.of(carcases.get(0));
    }

    private Optional<Carcase> assembleNewCarcase() {
        log.info("Trying to assemble new carcase");

        List<RawComponent> components = rawComponentRepository.takeComponents(
                new EntitySpecification(Casing.class),
                new EntitySpecification(ControlUnit.class)
        );

        if (components.isEmpty()) {
            return Optional.empty();
        }

        AssemblyOperationUtils.simulateAssemblyOperationDelay();

        return Optional.of(new Carcase(robotId, (Casing) components.get(0), (ControlUnit) components.get(0)));
    }
}

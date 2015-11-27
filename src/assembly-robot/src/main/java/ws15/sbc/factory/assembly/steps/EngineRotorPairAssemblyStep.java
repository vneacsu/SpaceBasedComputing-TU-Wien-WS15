package ws15.sbc.factory.assembly.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.assembly.AssemblyRobotLocalStorage;
import ws15.sbc.factory.common.repository.EntitySpecification;
import ws15.sbc.factory.common.repository.ProcessedComponentRepository;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.dto.Engine;
import ws15.sbc.factory.dto.EngineRotorPair;
import ws15.sbc.factory.dto.RawComponent;
import ws15.sbc.factory.dto.Rotor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class EngineRotorPairAssemblyStep extends TransactionalAssemblyStep {
    private static final Logger log = LoggerFactory.getLogger(EngineRotorPairAssemblyStep.class);

    private static final int N_ENGINE_ROTOR_PAIRS = 3;

    @Inject
    @Named("RobotId")
    private String robotId;
    @Inject
    private RawComponentRepository rawComponentRepository;
    @Inject
    private ProcessedComponentRepository processedComponentRepository;
    @Inject
    private AssemblyRobotLocalStorage assemblyRobotLocalStorage;

    @Override
    protected void performStepWithinTransaction() {
        log.info("Performing engine rotor pair assembly step");

        for (int i = 0; i < N_ENGINE_ROTOR_PAIRS; ++i) {
            Optional<EngineRotorPair> engineRotorPair = acquireOrAssembleEngineRotorPair();

            if (!engineRotorPair.isPresent()) {
                log.info("Engine rotor pair could have not been acquired/assembled");
                storeEngineRotorPairsForFutureUse();
                break;
            } else {
                log.info("Engine rotor pair successfully acquired/assembled");
                assemblyRobotLocalStorage.storeEngineRotorPair(engineRotorPair.get());
            }
        }
    }

    private Optional<EngineRotorPair> acquireOrAssembleEngineRotorPair() {
        Optional<EngineRotorPair> engineRotorPair = acquireEngineRotorPair();

        if (!engineRotorPair.isPresent()) {
            log.info("Existent engine rotor pair not found");
            engineRotorPair = assembleEngineRotorPair();
        }
        return engineRotorPair;
    }

    private Optional<EngineRotorPair> acquireEngineRotorPair() {
        log.info("Trying to acquire existent engine rotor pair...");

        List<EngineRotorPair> engineRotorPairs = processedComponentRepository.takeEntities(
                new EntitySpecification(EngineRotorPair.class)
        );

        return engineRotorPairs.isEmpty() ? Optional.empty() : Optional.of(engineRotorPairs.get(0));
    }

    private Optional<EngineRotorPair> assembleEngineRotorPair() {
        log.info("Assembling new engine rotor pair...");

        List<RawComponent> components = rawComponentRepository.takeEntities(
                new EntitySpecification(Engine.class),
                new EntitySpecification(Rotor.class)
        );

        if (components.isEmpty()) {
            return Optional.empty();
        }

        AssemblyOperationUtils.simulateAssemblyOperationDelay();

        return Optional.of(new EngineRotorPair(robotId, (Engine) components.get(0), (Rotor) components.get(0)));
    }

    private void storeEngineRotorPairsForFutureUse() {
        log.info("Storing available engine rotor pairs for future use...");

        List<EngineRotorPair> availableEngineRotorPairs = assemblyRobotLocalStorage.consumeEngineRotorPairs();
        EngineRotorPair[] engineRotorPairs = availableEngineRotorPairs.toArray(new EngineRotorPair[availableEngineRotorPairs.size()]);

        processedComponentRepository.storeEntities(engineRotorPairs);
    }
}

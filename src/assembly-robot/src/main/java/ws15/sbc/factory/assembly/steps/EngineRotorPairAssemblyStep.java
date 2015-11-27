package ws15.sbc.factory.assembly.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.assembly.AssemblyRobotLocalStorage;
import ws15.sbc.factory.common.repository.ProcessedComponentRepository;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.dto.Engine;
import ws15.sbc.factory.dto.EngineRotorPair;
import ws15.sbc.factory.dto.Rotor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class EngineRotorPairAssemblyStep implements AssemblyStep {
    private static final Logger log = LoggerFactory.getLogger(EngineRotorPairAssemblyStep.class);

    private static final int N_ENGINE_ROTOR_PAIRS = 3;

    @Inject
    @Named("RobotId")
    private String robotId;
    @Inject
    private TxManager txManager;
    @Inject
    private RawComponentRepository rawComponentRepository;
    @Inject
    private ProcessedComponentRepository processedComponentRepository;
    @Inject
    private AssemblyRobotLocalStorage assemblyRobotLocalStorage;

    @Override
    public void performStep() {
        log.info("Performing engine rotor pair assembly step");

        for (int i = 0; i < N_ENGINE_ROTOR_PAIRS; ++i) {
            txManager.beginTransaction();

            Optional<EngineRotorPair> engineRotorPair = acquireOrAssembleEngineRotorPair();

            if (!engineRotorPair.isPresent()) {
                log.info("Engine rotor pair could have not been acquired/assembled");
                txManager.rollback();

                storeEngineRotorPairsForFutureUse();
                break;
            }

            log.info("Engine rotor pair successfully acquired/assembled");
            assemblyRobotLocalStorage.storeEngineRotorPair(engineRotorPair.get());

            txManager.commit();
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

        return processedComponentRepository.takeOne(EngineRotorPair.class);
    }

    private Optional<EngineRotorPair> assembleEngineRotorPair() {
        log.info("Assembling new engine rotor pair...");

        Optional<Engine> engine = rawComponentRepository.takeOne(Engine.class);
        if (!engine.isPresent()) {
            return Optional.empty();
        }

        Optional<Rotor> rotor = rawComponentRepository.takeOne(Rotor.class);
        if (!rotor.isPresent()) {
            return Optional.empty();
        }

        AssemblyOperationUtils.simulateAssemblyOperationDelay();

        return Optional.of(new EngineRotorPair(robotId, engine.get(), rotor.get()));
    }

    private void storeEngineRotorPairsForFutureUse() {
        log.info("Storing available engine rotor pairs for future use...");

        List<EngineRotorPair> availableEngineRotorPairs = assemblyRobotLocalStorage.consumeEngineRotorPairs();

        processedComponentRepository.storeEntities(availableEngineRotorPairs);
    }
}

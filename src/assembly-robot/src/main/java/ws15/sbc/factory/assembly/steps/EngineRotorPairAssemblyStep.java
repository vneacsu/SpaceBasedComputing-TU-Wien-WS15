package ws15.sbc.factory.assembly.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.Engine;
import ws15.sbc.factory.common.dto.EngineRotorPair;
import ws15.sbc.factory.common.dto.Rotor;
import ws15.sbc.factory.common.repository.EntityMatcher;
import ws15.sbc.factory.common.repository.Repository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.common.utils.OperationUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Optional;

import static java.lang.Math.max;
import static ws15.sbc.factory.common.dto.Drone.N_REQUIRED_ENGINE_ROTOR_PAIRS;

@Singleton
public class EngineRotorPairAssemblyStep implements AssemblyStep {
    private static final Logger log = LoggerFactory.getLogger(EngineRotorPairAssemblyStep.class);

    @Inject
    @Named("RobotId")
    private String robotId;
    @Inject
    private TxManager txManager;
    @Inject
    private Repository repository;

    @Override
    public void performStep() {
        log.info("Performing engine rotor pair assembly step");

        for (int i = 0; i < getNumberOfEngineRotorPairsToAssemble(); ++i) {
            txManager.beginTransaction();

            Optional<EngineRotorPair> engineRotorPair = assembleEngineRotorPair();

            if (!engineRotorPair.isPresent()) {
                log.info("Engine rotor pair could have not been assembled");
                txManager.rollback();

                break;
            }

            log.info("Engine rotor pair successfully assembled");
            repository.storeEntity(engineRotorPair.get());

            txManager.commit();
        }
    }

    private int getNumberOfEngineRotorPairsToAssemble() {
        int nAvailableEngineRotorPairs = repository.count(EntityMatcher.of(EngineRotorPair.class), N_REQUIRED_ENGINE_ROTOR_PAIRS);

        return max(0, N_REQUIRED_ENGINE_ROTOR_PAIRS - nAvailableEngineRotorPairs);
    }

    private Optional<EngineRotorPair> assembleEngineRotorPair() {
        log.info("Assembling new engine rotor pair...");

        Optional<Engine> engine = repository.takeOne(EntityMatcher.of(Engine.class));
        if (!engine.isPresent()) {
            return Optional.empty();
        }

        Optional<Rotor> rotor = repository.takeOne(EntityMatcher.of(Rotor.class));
        if (!rotor.isPresent()) {
            return Optional.empty();
        }

        OperationUtils.simulateDelay(1000);

        return Optional.of(new EngineRotorPair(robotId, engine.get(), rotor.get()));
    }
}

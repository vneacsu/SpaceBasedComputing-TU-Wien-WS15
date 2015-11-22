package ws15.sbc.factory.assembly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.ComponentRepository;
import ws15.sbc.factory.common.repository.ComponentSpecification;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.dto.Component;
import ws15.sbc.factory.dto.Engine;
import ws15.sbc.factory.dto.EngineRotorPair;
import ws15.sbc.factory.dto.Rotor;

import java.util.List;
import java.util.Optional;

final class EngineRotorPairAssemblyStep implements AssemblyRobotStep {
    private static final Logger log = LoggerFactory.getLogger(EngineRotorPairAssemblyStep.class);

    private static final int N_ENGINE_ROTOR_PAIRS = 3;

    private final String robotId;
    private final ComponentRepository componentRepository;
    private final AssemblyRobotLocalStorage assemblyRobotLocalStorage;
    private final TxManager txManager;

    public EngineRotorPairAssemblyStep(AssemblyRobot assemblyRobot) {
        this.robotId = assemblyRobot.getRobotId();
        this.componentRepository = assemblyRobot.getComponentRepository();
        this.assemblyRobotLocalStorage = assemblyRobot.getAssemblyRobotLocalStorage();
        this.txManager = assemblyRobot.getTxManager();
    }

    @Override
    public void performStep() {
        log.info("Performing engine rotor pair assembly step");

        txManager.beginTransaction();

        try {
            acquireOrAssembleEngineRotorPairs();
        } catch (Exception e) {
            txManager.rollback();
            throw e;
        }

        txManager.commit();
    }

    private void acquireOrAssembleEngineRotorPairs() {
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

        List<EngineRotorPair> engineRotorPairs = componentRepository.takeComponents(
                new ComponentSpecification(EngineRotorPair.class)
        );

        return engineRotorPairs.isEmpty() ? Optional.empty() : Optional.of(engineRotorPairs.get(0));
    }

    private Optional<EngineRotorPair> assembleEngineRotorPair() {
        log.info("Assembling new engine rotor pair...");

        List<Component> components = componentRepository.takeComponents(
                new ComponentSpecification(Engine.class),
                new ComponentSpecification(Rotor.class)
        );

        if (components.isEmpty()) {
            return Optional.empty();
        }

        simulateAssemblyOperationDelay();

        return Optional.of(new EngineRotorPair(robotId, (Engine) components.get(0), (Rotor) components.get(0)));
    }

    private void simulateAssemblyOperationDelay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {
        }
    }

    private void storeEngineRotorPairsForFutureUse() {
        log.info("Storing available engine rotor pairs for future use...");

        List<EngineRotorPair> availableEngineRotorPairs = assemblyRobotLocalStorage.consumeEngineRotorPairs();
        EngineRotorPair[] engineRotorPairs = availableEngineRotorPairs.toArray(new EngineRotorPair[availableEngineRotorPairs.size()]);

        componentRepository.write(engineRotorPairs);
    }
}

package ws15.sbc.factory.assembly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.ComponentRepository;
import ws15.sbc.factory.common.repository.ComponentSpecification;
import ws15.sbc.factory.dto.Component;
import ws15.sbc.factory.dto.Engine;
import ws15.sbc.factory.dto.EngineRotorPair;
import ws15.sbc.factory.dto.Rotor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssemblyRobot {
    private static final Logger log = LoggerFactory.getLogger(AssemblyRobot.class);

    private static final int N_ENGINE_ROBOT_PAIRS = 3;

    private final String robotId;
    private final ComponentRepository componentRepository;

    private final List<EngineRotorPair> availableEngineRobotPairs = new ArrayList<>(N_ENGINE_ROBOT_PAIRS);

    public AssemblyRobot(String robotId, ComponentRepository componentRepository) {
        this.robotId = robotId;
        this.componentRepository = componentRepository;
    }

    public void run() {
        transactionalAcquireOrAssembleEngineRobotPairs();
        acquireOrAssembleCarcase();

        assembleDrone();
    }

    private void transactionalAcquireOrAssembleEngineRobotPairs() {
        componentRepository.beginTransaction();

        try {
            acquireOrAssembleEngineRobotPairs();

            componentRepository.commit();
        } catch (Exception e) {
            componentRepository.rollback();
            throw e;
        }
    }

    private void acquireOrAssembleEngineRobotPairs() {
        for (int i = 0; i < N_ENGINE_ROBOT_PAIRS; ++i) {
            Optional<EngineRotorPair> engineRotorPair = acquireOrAssembleEngineRotorPair();

            if (!engineRotorPair.isPresent()) {
                log.info("Engine rotor pair could have not been acquired/assembled");
                storeEngineRotorPairsForFutureUse();
                break;
            } else {
                log.info("Engine rotor pair successfully acquired/assembled");
                availableEngineRobotPairs.add(engineRotorPair.get());
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

        return components.isEmpty() ? Optional.empty() :
                Optional.of(new EngineRotorPair(robotId, (Engine) components.get(0), (Rotor) components.get(0)));
    }

    private void storeEngineRotorPairsForFutureUse() {
        log.info("Storing available engine rotor pairs for future use...");

        componentRepository.write(availableEngineRotorPairsArray());

        availableEngineRobotPairs.clear();
    }

    private EngineRotorPair[] availableEngineRotorPairsArray() {
        return availableEngineRobotPairs.toArray(new EngineRotorPair[availableEngineRobotPairs.size()]);
    }

    private void acquireOrAssembleCarcase() {

    }

    private void assembleDrone() {
    }
}

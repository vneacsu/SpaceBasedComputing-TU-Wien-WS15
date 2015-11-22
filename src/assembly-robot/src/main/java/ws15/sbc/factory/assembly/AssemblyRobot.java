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
                storeEngineRotorPairsForFutureUse();
                break;
            } else {
                availableEngineRobotPairs.add(engineRotorPair.get());
            }
        }
    }

    private Optional<EngineRotorPair> acquireOrAssembleEngineRotorPair() {
        Optional<EngineRotorPair> engineRotorPair = acquireEngineRotorPair();

        if (!engineRotorPair.isPresent()) {
            engineRotorPair = assembleEngineRotorPair();
        }
        return engineRotorPair;
    }

    private Optional<EngineRotorPair> acquireEngineRotorPair() {
        List<EngineRotorPair> engineRotorPairs = componentRepository.takeComponents(
                new ComponentSpecification(EngineRotorPair.class)
        );

        return engineRotorPairs.isEmpty() ? Optional.empty() : Optional.of(engineRotorPairs.get(0));
    }

    private Optional<EngineRotorPair> assembleEngineRotorPair() {
        List<Component> components = componentRepository.takeComponents(
                new ComponentSpecification(Engine.class),
                new ComponentSpecification(Rotor.class)
        );

        return components.isEmpty() ? Optional.empty() :
                Optional.of(new EngineRotorPair(robotId, (Engine) components.get(0), (Rotor) components.get(0)));
    }

    private void storeEngineRotorPairsForFutureUse() {
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

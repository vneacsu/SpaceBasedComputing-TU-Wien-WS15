package ws15.sbc.factory.assembly;

import ws15.sbc.factory.dto.EngineRotorPair;

import java.util.ArrayList;
import java.util.List;

public class AssemblyRobotLocalStorage {

    private final List<EngineRotorPair> availableEngineRotorPairs = new ArrayList<>();

    public void storeEngineRotorPair(EngineRotorPair engineRotorPair) {
        availableEngineRotorPairs.add(engineRotorPair);
    }

    public List<EngineRotorPair> consumeEngineRotorPairs() {
        List<EngineRotorPair> result = new ArrayList<>(availableEngineRotorPairs);

        availableEngineRotorPairs.clear();

        return result;
    }
}

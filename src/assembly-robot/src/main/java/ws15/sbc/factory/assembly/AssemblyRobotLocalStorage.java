package ws15.sbc.factory.assembly;

import com.google.common.base.Preconditions;
import ws15.sbc.factory.dto.Carcase;
import ws15.sbc.factory.dto.EngineRotorPair;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class AssemblyRobotLocalStorage {

    private static final int ENGINE_ROTOR_PAIRS_CAPACITY = 3;
    private static final int CARCASES_CAPACITY = 1;

    private final List<EngineRotorPair> availableEngineRotorPairs = new ArrayList<>(ENGINE_ROTOR_PAIRS_CAPACITY);

    private final List<Carcase> availableCarcases = new ArrayList<>(CARCASES_CAPACITY);

    public void storeEngineRotorPair(EngineRotorPair engineRotorPair) {
        Preconditions.checkState(availableEngineRotorPairs.size() < ENGINE_ROTOR_PAIRS_CAPACITY,
                "Should only store maximal %s engine rotor pairs", ENGINE_ROTOR_PAIRS_CAPACITY);

        availableEngineRotorPairs.add(engineRotorPair);
    }

    public List<EngineRotorPair> consumeEngineRotorPairs() {
        List<EngineRotorPair> result = new ArrayList<>(availableEngineRotorPairs);

        availableEngineRotorPairs.clear();

        return result;
    }

    public void storeCarcase(Carcase carcase) {
        Preconditions.checkState(availableCarcases.size() < CARCASES_CAPACITY,
                "Should only store maximal %s carcase in local storage", CARCASES_CAPACITY);

        availableCarcases.add(carcase);
    }
}

package ws15.sbc.factory.assembly;

import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.dto.Carcase;
import ws15.sbc.factory.dto.EngineRotorPair;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class AssemblyRobotLocalStorage {

    private static final Logger log = LoggerFactory.getLogger(AssemblyRobotLocalStorage.class);

    private static final int ENGINE_ROTOR_PAIRS_CAPACITY = 3;

    private final List<EngineRotorPair> availableEngineRotorPairs = new ArrayList<>(ENGINE_ROTOR_PAIRS_CAPACITY);

    private Optional<Carcase> availableCarcase = Optional.empty();

    public void storeEngineRotorPair(@NotNull EngineRotorPair engineRotorPair) {
        Preconditions.checkState(availableEngineRotorPairs.size() < ENGINE_ROTOR_PAIRS_CAPACITY,
                "Should only store maximal %s engine rotor pairs", ENGINE_ROTOR_PAIRS_CAPACITY);

        log.info("Storing engine rotor pair {} into local storage", engineRotorPair);

        availableEngineRotorPairs.add(engineRotorPair);
    }

    public List<EngineRotorPair> consumeEngineRotorPairs() {
        List<EngineRotorPair> result = new ArrayList<>(availableEngineRotorPairs);

        availableEngineRotorPairs.clear();

        return result;
    }

    public void storeCarcase(@NotNull Carcase carcase) {
        Preconditions.checkState(!availableCarcase.isPresent(),
                "Should only store maximal 1 carcase in local storage");

        log.info("Storing carcase {} into local storage", carcase);

        availableCarcase = Optional.of(carcase);
    }

    public Optional<Carcase> consumeCarcase() {
        Optional<Carcase> carcase = availableCarcase;

        availableCarcase = Optional.empty();

        return carcase;
    }
}

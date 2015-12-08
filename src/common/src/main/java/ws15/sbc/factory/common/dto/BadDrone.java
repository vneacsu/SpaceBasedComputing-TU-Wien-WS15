package ws15.sbc.factory.common.dto;

import java.util.List;

public class BadDrone extends Drone {

    public BadDrone(String assembledBy, List<EngineRotorPair> engineRotorPairs, Carcase carcase) {
        super(assembledBy, engineRotorPairs, carcase);
    }

}

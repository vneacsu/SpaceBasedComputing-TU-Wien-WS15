package ws15.sbc.factory.common.dto;

import java.util.List;

public class GoodDrone extends Drone {

    public GoodDrone(String assembledBy, List<EngineRotorPair> engineRotorPairs, Carcase carcase) {
        super(assembledBy, engineRotorPairs, carcase);
    }

}

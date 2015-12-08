package ws15.sbc.factory.common.dto;

import java.util.List;

public class CalibratedDrone extends Drone {

    public CalibratedDrone(String assembledBy, List<EngineRotorPair> engineRotorPairs, Carcase carcase) {
        super(assembledBy, engineRotorPairs, carcase);
    }
}

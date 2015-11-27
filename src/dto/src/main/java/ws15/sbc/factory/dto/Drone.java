package ws15.sbc.factory.dto;

import java.util.List;

public class Drone extends ProcessedComponent {

    private final List<EngineRotorPair> engineRotorPairs;
    private final Carcase carcase;

    public Drone(String assembledBy, List<EngineRotorPair> engineRotorPairs, Carcase carcase) {
        super(assembledBy);

        this.engineRotorPairs = engineRotorPairs;
        this.carcase = carcase;
    }
}

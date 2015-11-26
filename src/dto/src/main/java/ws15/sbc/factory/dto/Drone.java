package ws15.sbc.factory.dto;

import java.util.List;

public class Drone extends ProcessedComponent {

    private final String assembledBy;
    private final List<EngineRotorPair> engineRotorPairs;
    private final Carcase carcase;

    public Drone(String assembledBy, List<EngineRotorPair> engineRotorPairs, Carcase carcase) {
        this.assembledBy = assembledBy;
        this.engineRotorPairs = engineRotorPairs;
        this.carcase = carcase;
    }

    public String getAssembledBy() {
        return assembledBy;
    }

    public List<EngineRotorPair> getEngineRotorPairs() {
        return engineRotorPairs;
    }

    public Carcase getCarcase() {
        return carcase;
    }

    @Override
    public String toString() {
        return "Drone{" +
                "assembledBy='" + assembledBy + '\'' +
                ", engineRotorPairs=" + engineRotorPairs +
                ", carcase=" + carcase +
                "} " + super.toString();
    }
}

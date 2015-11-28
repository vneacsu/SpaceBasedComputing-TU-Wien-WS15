package ws15.sbc.factory.common.dto;

import java.util.List;

public class Drone extends ProcessedComponent {

    private List<EngineRotorPair> engineRotorPairs;
    private final Carcase carcase;

    public Drone(String assembledBy, List<EngineRotorPair> engineRotorPairs, Carcase carcase) {
        super(assembledBy);

        this.engineRotorPairs = engineRotorPairs;
        this.carcase = carcase;
    }

    public void setEngineRotorPairs(List<EngineRotorPair> engineRotorPairs) {
        this.engineRotorPairs = engineRotorPairs;
    }

    public List<EngineRotorPair> getEngineRotorPairs() {
        return engineRotorPairs;
    }

    public void setCalibrationSum(Integer calibrationSum) {
        carcase.setCalibrationSum(calibrationSum);
    }

    public Integer getCalibrationSum() {
        return carcase.getCalibrationSum();
    }
}

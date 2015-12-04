package ws15.sbc.factory.common.dto;

import java.util.List;

public class Drone extends ProcessedComponent {

    private List<EngineRotorPair> engineRotorPairs;
    private final Carcase carcase;
    private String testedBy;

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

    public void calibrate(Integer calibrationSum, String calibratedBy) {
        carcase.setCalibrationSum(calibrationSum);
        carcase.setCalibratedBy(calibratedBy);
    }

    public Carcase getCarcase() {
        return carcase;
    }

    public Integer getCalibrationSum() {
        return carcase.getCalibrationSum();
    }

    public void setTestedBy(String testedBy) {
        this.testedBy = testedBy;
    }

    public String getTestedBy() {
        return testedBy;
    }
}

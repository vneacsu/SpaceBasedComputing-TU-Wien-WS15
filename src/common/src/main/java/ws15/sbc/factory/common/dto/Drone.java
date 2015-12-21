package ws15.sbc.factory.common.dto;

import com.google.common.base.Preconditions;

import java.util.List;

public class Drone extends ProcessedComponent {

    public static final int N_REQUIRED_CARCASES = 1;
    public static final int N_REQUIRED_ENGINE_ROTOR_PAIRS = 3;

    public static final String IS_CALIBRATED_PATH = "carcase.calibratedBy";
    public static final String TESTED_BY_FIELD = "testedBy";
    public static final String IS_GOOD_DRONE_FIELD = "goodDrone";

    private final List<EngineRotorPair> engineRotorPairs;
    private final Carcase carcase;
    private String testedBy;
    private boolean goodDrone = false;

    public Drone(String assembledBy, List<EngineRotorPair> engineRotorPairs, Carcase carcase) {
        super(assembledBy);

        this.engineRotorPairs = engineRotorPairs;
        this.carcase = carcase;
    }

    public void calibrate(String calibratedBy, int calibrationSum) {
        Preconditions.checkState(!carcase.isCalibrated(), "Drone already calibrated!");

        carcase.calibrate(calibratedBy, calibrationSum);
    }

    public void setTestResult(String testedBy, boolean isGoodDrone) {
        Preconditions.checkState(!isTested(), "Drone already tested!");

        this.testedBy = testedBy;
        this.goodDrone = isGoodDrone;
    }

    public Integer getCalibrationSum() {
        return carcase.getCalibrationSum();
    }

    public boolean isTested() {
        return testedBy != null;
    }

    public List<EngineRotorPair> getEngineRotorPairs() {
        return engineRotorPairs;
    }

    public Carcase getCarcase() {
        return carcase;
    }

    public String getTestedBy() {
        return testedBy;
    }

    public boolean isGoodDrone() {
        return goodDrone;
    }
}

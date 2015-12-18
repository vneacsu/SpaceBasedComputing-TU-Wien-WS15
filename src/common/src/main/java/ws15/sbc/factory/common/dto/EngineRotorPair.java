package ws15.sbc.factory.common.dto;

import com.google.common.base.Preconditions;

public class EngineRotorPair extends ProcessedComponent {

    private final Engine engine;
    private final Rotor rotor;
    private Integer calibrationValue = null;
    private String calibratedBy = null;

    public EngineRotorPair(String assembledBy, Engine engine, Rotor rotor) {
        super(assembledBy);

        this.engine = engine;
        this.rotor = rotor;
    }

    public void calibrate(String calibratedBy, int calibrationValue) {
        Preconditions.checkState(!isCalibrated(), "Engine rotor pair already calibrated!");

        this.calibratedBy = calibratedBy;
        this.calibrationValue = calibrationValue;
    }

    public Engine getEngine() {
        return engine;
    }

    public Rotor getRotor() {
        return rotor;
    }

    public Integer getCalibrationValue() {
        return calibrationValue;
    }

    public String getCalibratedBy() {
        return calibratedBy;
    }

    public boolean isCalibrated() {
        return calibratedBy != null;
    }

    @Override
    protected String getComponentName() {
        return isCalibrated() ? "Calibrated-EngineRotorPair" : "Uncalibrated-EngineRotorPair";
    }
}

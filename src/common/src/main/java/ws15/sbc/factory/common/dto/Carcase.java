package ws15.sbc.factory.common.dto;

import com.google.common.base.Preconditions;

public class Carcase extends ProcessedComponent {

    public static final String CASING_FIELD = "casing";

    private final Casing casing;
    private final ControlUnit controlUnit;
    private Integer calibrationSum = null;
    private String calibratedBy = null;

    public Carcase(String assembledBy, Casing casing, ControlUnit controlUnit) {
        super(assembledBy);

        this.casing = casing;
        this.controlUnit = controlUnit;
    }

    public void calibrate(String calibratedBy, int calibrationSum) {
        Preconditions.checkState(!isCalibrated(), "Carcaase already calibrated!");

        this.calibratedBy = calibratedBy;
        this.calibrationSum = calibrationSum;
    }

    public boolean isCalibrated() {
        return calibratedBy != null;
    }

    public Casing getCasing() {
        return casing;
    }

    public ControlUnit getControlUnit() {
        return controlUnit;
    }

    public Integer getCalibrationSum() {
        return calibrationSum;
    }

    public String getCalibratedBy() {
        return calibratedBy;
    }

    public void setCalibratedBy(String calibratedBy) {
        this.calibratedBy = calibratedBy;
    }
}

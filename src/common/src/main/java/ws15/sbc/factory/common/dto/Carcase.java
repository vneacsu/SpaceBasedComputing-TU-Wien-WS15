package ws15.sbc.factory.common.dto;

public class Carcase extends ProcessedComponent {

    private final Casing casing;
    private final ControlUnit controlUnit;
    private Integer calibrationSum = null;
    private String calibratedBy = null;

    public Carcase(String assembledBy, Casing casing, ControlUnit controlUnit) {
        super(assembledBy);

        this.casing = casing;
        this.controlUnit = controlUnit;
    }

    public void setCalibrationSum(Integer calibrationSum) {
        this.calibrationSum = calibrationSum;
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

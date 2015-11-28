package ws15.sbc.factory.common.dto;

public class Carcase extends ProcessedComponent {

    private final Casing casing;
    private final ControlUnit controlUnit;
    private Integer calibrationSum = null;

    public Carcase(String assembledBy, Casing casing, ControlUnit controlUnit) {
        super(assembledBy);

        this.casing = casing;
        this.controlUnit = controlUnit;
    }

    public void setCalibrationSum(Integer calibrationSum) {
        this.calibrationSum = calibrationSum;
    }

    public Integer getCalibrationSum() {
        return calibrationSum;
    }
}

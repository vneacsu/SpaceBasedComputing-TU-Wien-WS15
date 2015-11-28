package ws15.sbc.factory.dto;

public abstract class EngineRotorPair extends ProcessedComponent {

    private final Engine engine;
    private final Rotor rotor;
    private Integer calibrationValue = null;

    public EngineRotorPair(String assembledBy, Engine engine, Rotor rotor) {
        super(assembledBy);

        this.engine = engine;
        this.rotor = rotor;
    }

    public void setCalibrationValue(Integer calibrationValue) {
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

    abstract public boolean isCalibrated();

    abstract public EngineRotorPair calibrate();
}

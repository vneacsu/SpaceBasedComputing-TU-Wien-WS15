package ws15.sbc.factory.dto;

public class Carcase extends ProcessedComponent {

    private final String assembledBy;
    private final Casing casing;
    private final ControlUnit controlUnit;

    public Carcase(String assembledBy, Casing casing, ControlUnit controlUnit) {
        this.assembledBy = assembledBy;
        this.casing = casing;
        this.controlUnit = controlUnit;
    }

    public String getAssembledBy() {
        return assembledBy;
    }

    public Casing getCasing() {
        return casing;
    }

    public ControlUnit getControlUnit() {
        return controlUnit;
    }

    @Override
    public String toString() {
        return "EngineRotorPair{" +
                "assembledBy='" + assembledBy + '\'' +
                ", casing=" + casing +
                ", controlUnit=" + controlUnit +
                "} " + super.toString();
    }
}

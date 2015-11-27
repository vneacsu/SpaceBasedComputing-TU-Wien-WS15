package ws15.sbc.factory.dto;

public class Carcase extends ProcessedComponent {

    private final Casing casing;
    private final ControlUnit controlUnit;

    public Carcase(String assembledBy, Casing casing, ControlUnit controlUnit) {
        super(assembledBy);

        this.casing = casing;
        this.controlUnit = controlUnit;
    }
}

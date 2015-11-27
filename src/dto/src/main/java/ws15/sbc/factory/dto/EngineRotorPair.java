package ws15.sbc.factory.dto;

public class EngineRotorPair extends ProcessedComponent {

    private final Engine engine;
    private final Rotor rotor;

    public EngineRotorPair(String assembledBy, Engine engine, Rotor rotor) {
        super(assembledBy);

        this.engine = engine;
        this.rotor = rotor;
    }
}

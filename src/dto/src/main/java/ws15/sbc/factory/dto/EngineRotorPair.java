package ws15.sbc.factory.dto;

public class EngineRotorPair extends ProcessedComponent {

    private final String assembledBy;
    private final Engine engine;
    private final Rotor rotor;

    public EngineRotorPair(String assembledBy, Engine engine, Rotor rotor) {
        this.assembledBy = assembledBy;
        this.engine = engine;
        this.rotor = rotor;
    }

    public String getAssembledBy() {
        return assembledBy;
    }

    public Engine getEngine() {
        return engine;
    }

    public Rotor getRotor() {
        return rotor;
    }

    @Override
    public String toString() {
        return "EngineRotorPair{" +
                "assembledBy='" + assembledBy + '\'' +
                ", engine=" + engine +
                ", rotor=" + rotor +
                "} " + super.toString();
    }
}

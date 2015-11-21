package ws15.sbc.factory.supply;

import ws15.sbc.factory.common.repository.ComponentRepository;
import ws15.sbc.factory.common.repository.ComponentRepositoryProducer;
import ws15.sbc.factory.dto.factory.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Main {

    private final static ComponentFactory caseFactory = new CaseFactory();
    private final static ComponentFactory controlUnitFactory = new ControlUnitFactory();
    private final static ComponentFactory engineFactory = new EngineFactory();
    private final static ComponentFactory rotorFactory = new RotorFactory();
    private final static Map<String, ComponentFactory> componentFactoryMap;

    static {
        componentFactoryMap = new HashMap<>();
        componentFactoryMap.put("case", caseFactory);
        componentFactoryMap.put("controlUnit", controlUnitFactory);
        componentFactoryMap.put("engine", engineFactory);
        componentFactoryMap.put("rotor", rotorFactory);
    }

    public static void main(String[] argv) {
        ComponentRepository componentRepository = ComponentRepositoryProducer.getSingleton();

        Optional<String> componentType = getProperty("componentType");
        ComponentFactory componentFactory = componentFactoryMap.get(componentType.orElseThrow(() -> new IllegalArgumentException("No component type specified")));

        Integer quantity = Integer.parseInt(getProperty("quantity").orElseThrow(() -> new IllegalArgumentException("No quantity specified")));
        Long interval = Long.parseLong(getProperty("interval").orElseThrow(() -> new IllegalArgumentException("No interval specified")));

        new SupplyRobot(componentRepository, componentFactory, quantity, interval).run();
    }

    private static Optional<String> getProperty(String s) {
        final String value = System.getProperty(s);
        if (value != null) {
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }
}

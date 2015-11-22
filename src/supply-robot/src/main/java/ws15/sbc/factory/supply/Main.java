package ws15.sbc.factory.supply;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ws15.sbc.factory.common.CommonModule;
import ws15.sbc.factory.common.app.AppManager;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.dto.factory.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Main {

    private final static RawComponentFactory caseFactory = new CaseFactory();
    private final static RawComponentFactory controlUnitFactory = new ControlUnitFactory();
    private final static RawComponentFactory engineFactory = new EngineFactory();
    private final static RawComponentFactory rotorFactory = new RotorFactory();
    private final static Map<String, RawComponentFactory> componentFactoryMap;

    static {
        componentFactoryMap = new HashMap<>();
        componentFactoryMap.put("case", caseFactory);
        componentFactoryMap.put("controlUnit", controlUnitFactory);
        componentFactoryMap.put("engine", engineFactory);
        componentFactoryMap.put("rotor", rotorFactory);
    }

    public static void main(String[] argv) {
        Injector injector = Guice.createInjector(new CommonModule());

        RawComponentRepository rawComponentRepository = injector.getInstance(RawComponentRepository.class);

        Optional<String> componentType = getProperty("componentType");
        RawComponentFactory rawComponentFactory = componentFactoryMap.get(componentType.orElseThrow(() -> new IllegalArgumentException("No component type specified")));

        Integer quantity = Integer.parseInt(getProperty("quantity").orElseThrow(() -> new IllegalArgumentException("No quantity specified")));
        Long interval = Long.parseLong(getProperty("interval").orElseThrow(() -> new IllegalArgumentException("No interval specified")));

        new SupplyRobot(rawComponentRepository, rawComponentFactory, quantity, interval).run();

        injector.getInstance(AppManager.class).shutdown();
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

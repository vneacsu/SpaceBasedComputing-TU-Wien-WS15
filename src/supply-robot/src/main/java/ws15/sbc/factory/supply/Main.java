package ws15.sbc.factory.supply;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ws15.sbc.factory.common.CommonModule;
import ws15.sbc.factory.common.app.AppManager;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.dto.factory.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.util.Arrays.asList;
import static ws15.sbc.factory.common.utils.PropertyUtils.getProperty;

public class Main {

    private static final Random randomizer = new Random();

    private final static List<String> rawComponentTypes = asList("casing", "control-unit", "engine", "rotor");

    private final static List<RawComponentFactory> rawComponentFactories =
            asList(new CasingFactory(), new ControlUnitFactory(), new EngineFactory(), new RotorFactory());
    private final static Map<String, RawComponentFactory> rawComponentFactoryMap = new HashMap<>();
    static {
        for (int i = 0; i < rawComponentTypes.size(); ++i) {
            rawComponentFactoryMap.put(rawComponentTypes.get(i), rawComponentFactories.get(i));
        }
    }

    private static final String robotId = getProperty("robotId").orElse("Supply Robot " + randomizer.nextInt(10000));
    private static final String componentType = getProperty("componentType")
            .orElse(rawComponentTypes.get(randomizer.nextInt(rawComponentTypes.size())));
    private static final Integer quantity = parseInt(getProperty("quantity").orElse("" + (randomizer.nextInt(9) + 1)));
    private static final Long interval = parseLong(getProperty("interval").orElse("" + randomizer.nextInt(1000)));

    public static void main(String[] argv) {
        Injector injector = Guice.createInjector(new CommonModule());

        RawComponentFactory rawComponentFactory = rawComponentFactoryMap.get(componentType);
        RawComponentRepository rawComponentRepository = injector.getInstance(RawComponentRepository.class);

        new SupplyRobot(robotId, rawComponentRepository, rawComponentFactory, quantity, interval).run();

        injector.getInstance(AppManager.class).shutdown();
    }
}

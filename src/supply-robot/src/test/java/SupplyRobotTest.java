import org.junit.Test;
import ws15.sbc.factory.common.repository.ComponentRepository;
import ws15.sbc.factory.dto.factory.*;
import ws15.sbc.factory.supply.SupplyRobot;

import static org.mockito.Mockito.*;

public class SupplyRobotTest {

    private static final ComponentFactory caseFactory = new CaseFactory();
    private static final ComponentFactory controlUnitFactory = new ControlUnitFactory();
    private static final ComponentFactory rotorFactory = new RotorFactory();
    private static final ComponentFactory engineFactory = new EngineFactory();

    @Test
    public void deliver3Rotors_shouldBeSuccessful() {
        ComponentRepository componentRepository = mock(ComponentRepository.class);

        new SupplyRobot(componentRepository, rotorFactory, 3, 100L).run();

        verify(componentRepository, times(3)).write(anyObject());
        verify(componentRepository, times(1)).close();
    }

}
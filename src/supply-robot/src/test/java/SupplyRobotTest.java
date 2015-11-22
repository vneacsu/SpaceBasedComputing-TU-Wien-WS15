import org.junit.Test;
import ws15.sbc.factory.common.repository.ComponentRepository;
import ws15.sbc.factory.dto.factory.ComponentFactory;
import ws15.sbc.factory.dto.factory.RotorFactory;
import ws15.sbc.factory.supply.SupplyRobot;

import static org.mockito.Mockito.*;

public class SupplyRobotTest {

    private static final ComponentFactory rotorFactory = new RotorFactory();

    @Test
    public void deliver3Rotors_shouldBeSuccessful() {
        ComponentRepository componentRepository = mock(ComponentRepository.class);

        new SupplyRobot(componentRepository, rotorFactory, 3, 100L).run();

        verify(componentRepository, times(3)).write(anyObject());
    }

}
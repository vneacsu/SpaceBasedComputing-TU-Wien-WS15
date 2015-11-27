import org.junit.Test;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.dto.factory.RawComponentFactory;
import ws15.sbc.factory.dto.factory.RotorFactory;
import ws15.sbc.factory.supply.SupplyRobot;

import static org.mockito.Mockito.*;

public class SupplyRobotTest {

    private static final RawComponentFactory rotorFactory = new RotorFactory();

    @Test
    public void deliver3Rotors_shouldBeSuccessful() {
        RawComponentRepository rawComponentRepository = mock(RawComponentRepository.class);

        new SupplyRobot("", rawComponentRepository, rotorFactory, 3, 100L).run();

        verify(rawComponentRepository, times(3)).storeEntity(anyObject());
    }

}
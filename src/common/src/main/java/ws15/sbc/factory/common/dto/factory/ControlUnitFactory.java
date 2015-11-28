package ws15.sbc.factory.common.dto.factory;

import ws15.sbc.factory.common.dto.ControlUnit;
import ws15.sbc.factory.common.dto.RawComponent;

public class ControlUnitFactory implements RawComponentFactory {

    @Override
    public RawComponent produceRawComponent(String robotId) {
        return new ControlUnit(robotId);
    }

}

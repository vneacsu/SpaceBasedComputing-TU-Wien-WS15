package ws15.sbc.factory.dto.factory;

import ws15.sbc.factory.dto.ControlUnit;
import ws15.sbc.factory.dto.RawComponent;

public class ControlUnitFactory implements RawComponentFactory {

    @Override
    public RawComponent produceRawComponent() {
        return new ControlUnit();
    }

}

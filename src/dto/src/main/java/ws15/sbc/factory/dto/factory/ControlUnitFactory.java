package ws15.sbc.factory.dto.factory;

import ws15.sbc.factory.dto.Component;
import ws15.sbc.factory.dto.ControlUnit;

public class ControlUnitFactory implements ComponentFactory {

    @Override
    public Component produceComponent() {
        return new ControlUnit();
    }

}

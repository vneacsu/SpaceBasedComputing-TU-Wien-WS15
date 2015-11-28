package ws15.sbc.factory.common.dto.factory;

import ws15.sbc.factory.common.dto.RawComponent;
import ws15.sbc.factory.common.dto.Rotor;

public class RotorFactory implements RawComponentFactory {

    @Override
    public RawComponent produceRawComponent(String robotId) {
        return new Rotor(robotId);
    }

}

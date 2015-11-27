package ws15.sbc.factory.dto.factory;

import ws15.sbc.factory.dto.Casing;
import ws15.sbc.factory.dto.RawComponent;

public class CasingFactory implements RawComponentFactory {
    @Override
    public RawComponent produceRawComponent(String robotId) {
        return new Casing(robotId);
    }
}

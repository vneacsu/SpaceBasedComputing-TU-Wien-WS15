package ws15.sbc.factory.common.dto.factory;

import ws15.sbc.factory.common.dto.Casing;
import ws15.sbc.factory.common.dto.RawComponent;

public class CasingFactory implements RawComponentFactory {
    @Override
    public RawComponent produceRawComponent(String robotId) {
        return new Casing(robotId);
    }
}

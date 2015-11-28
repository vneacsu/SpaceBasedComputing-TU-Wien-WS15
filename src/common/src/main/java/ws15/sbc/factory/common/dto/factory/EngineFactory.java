package ws15.sbc.factory.common.dto.factory;

import ws15.sbc.factory.common.dto.Engine;
import ws15.sbc.factory.common.dto.RawComponent;

public class EngineFactory implements RawComponentFactory {

    @Override
    public RawComponent produceRawComponent(String robotId) {
        return new Engine(robotId);
    }

}

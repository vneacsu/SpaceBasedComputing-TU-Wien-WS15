package ws15.sbc.factory.dto.factory;

import ws15.sbc.factory.dto.Engine;
import ws15.sbc.factory.dto.RawComponent;

public class EngineFactory implements RawComponentFactory {

    @Override
    public RawComponent produceRawComponent() {
        return new Engine();
    }

}

package ws15.sbc.factory.dto.factory;

import ws15.sbc.factory.dto.Component;
import ws15.sbc.factory.dto.Engine;

public class EngineFactory implements ComponentFactory {

    @Override
    public Component produceComponent() {
        return new Engine();
    }

}

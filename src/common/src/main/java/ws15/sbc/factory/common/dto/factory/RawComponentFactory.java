package ws15.sbc.factory.common.dto.factory;

import ws15.sbc.factory.common.dto.RawComponent;

public interface RawComponentFactory {

    RawComponent produceRawComponent(String robotId);
}

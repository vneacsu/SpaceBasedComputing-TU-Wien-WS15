package ws15.sbc.factory.dto.factory;

import ws15.sbc.factory.dto.Case;
import ws15.sbc.factory.dto.RawComponent;

public class CaseFactory implements RawComponentFactory {
    @Override
    public RawComponent produceRawComponent() {
        return new Case();
    }
}

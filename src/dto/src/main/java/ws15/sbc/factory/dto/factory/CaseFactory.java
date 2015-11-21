package ws15.sbc.factory.dto.factory;

import ws15.sbc.factory.dto.Case;
import ws15.sbc.factory.dto.Component;

public class CaseFactory implements ComponentFactory {
    @Override
    public Component produceComponent() {
        return new Case();
    }
}

package ws15.sbc.factory.dto.factory;

import ws15.sbc.factory.dto.RawComponent;
import ws15.sbc.factory.dto.Rotor;

public class RotorFactory implements RawComponentFactory {

    @Override
    public RawComponent produceRawComponent() {
        return new Rotor();
    }

}

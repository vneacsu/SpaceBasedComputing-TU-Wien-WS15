package ws15.sbc.factory.dto.factory;

import ws15.sbc.factory.dto.Component;
import ws15.sbc.factory.dto.Rotor;

public class RotorFactory implements ComponentFactory {

    @Override
    public Component produceComponent() {
        return new Rotor();
    }

}

package ws15.sbc.factory.common.dto;

import java.util.Random;

public class UnCalibratedEngineRotorPair extends EngineRotorPair {

    private static final Random random = new Random();

    public UnCalibratedEngineRotorPair(String assembledBy,
                                       Engine engine,
                                       Rotor rotor) {
        super(assembledBy, engine, rotor);
    }

    public EngineRotorPair calibrate() {
        Integer calibrationValue = random.nextInt(21) - 10;

        return new CalibratedEngineRotorPair(getAssembledBy(), getEngine(), getRotor(), calibrationValue);
    }

    @Override
    public boolean isCalibrated() {
        return false;
    }

}

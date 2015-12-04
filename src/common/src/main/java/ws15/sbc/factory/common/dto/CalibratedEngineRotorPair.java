package ws15.sbc.factory.common.dto;

public class CalibratedEngineRotorPair extends EngineRotorPair {

    public CalibratedEngineRotorPair(String assembledBy,
                                     Engine engine,
                                     Rotor rotor,
                                     Integer calibrationValue) {

        super(assembledBy, engine, rotor);
        setCalibrationValue(calibrationValue);
    }

    @Override
    public boolean isCalibrated() {
        return true;
    }

    @Override
    public EngineRotorPair calibrate(String calibratedBy) {
        throw new IllegalStateException("Engine-rotor pair already calibrated");
    }
}

package ws15.sbc.factory.calibrate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.Drone;
import ws15.sbc.factory.common.dto.EngineRotorPair;
import ws15.sbc.factory.common.repository.EntityMatcher;
import ws15.sbc.factory.common.repository.Repository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.common.utils.OperationUtils;
import ws15.sbc.factory.common.utils.PropertyUtils;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static ws15.sbc.factory.common.dto.Drone.IS_CALIBRATED_PATH;
import static ws15.sbc.factory.common.dto.ProcessedComponent.CALIBRATED_BY_FIELD;

public class CalibrateRobot {

    private static final Logger log = LoggerFactory.getLogger(CalibrateRobot.class);

    private static final Random random = new Random();

    private final String robotId;
    private final AtomicBoolean keepWorking = new AtomicBoolean(true);

    @Inject
    private Repository repository;

    @Inject
    private TxManager txManager;

    public CalibrateRobot() {
        robotId = PropertyUtils.getProperty("robotId").orElse(UUID.randomUUID().toString());
    }

    public void run() {
        while (keepWorking.get()) {
            if (random.nextBoolean()) {
                takeProcessedComponent();
            } else {
                takeDrone();
            }
        }
    }

    private void takeProcessedComponent() {
        log.info("Looking for a engine-rotor pair to calibrate...");
        txManager.beginTransaction();

        Optional<EngineRotorPair> engineRotorPair = repository.takeOne(notCalibratedEngineRotorPairMatcher());
        if (engineRotorPair.isPresent()) {
            calibrateEngineRotorPair(engineRotorPair.get());

            repository.storeEntity(engineRotorPair.get());
            log.info("Engine-rotor pair has been calibrated and stored");
        } else {
            log.info("No engine-rotor pair found");
        }

        txManager.commit();
    }

    private void calibrateEngineRotorPair(EngineRotorPair engineRotorPair) {
        engineRotorPair.calibrate(robotId, nextCalibrationValue());
        OperationUtils.simulateDelay(1000);
    }

    private EntityMatcher<EngineRotorPair> notCalibratedEngineRotorPairMatcher() {
        return EntityMatcher.of(EngineRotorPair.class).withNullField(CALIBRATED_BY_FIELD);
    }

    private int nextCalibrationValue() {
        return random.nextInt(21) - 10;
    }

    private void takeDrone() {
        log.info("Looking for a drone...");
        txManager.beginTransaction();

        Optional<Drone> drone = repository.takeOne(notCalibratedDroneMatcher());
        if (drone.isPresent()) {
            calibrateDrone(drone.get());
            repository.storeEntity(drone.get());
            log.info("Drone has been calibrated and stored");
        } else {
            log.info("No drone found");
        }

        txManager.commit();
    }

    private EntityMatcher<Drone> notCalibratedDroneMatcher() {
        return EntityMatcher.of(Drone.class).withNullField(IS_CALIBRATED_PATH);
    }

    private void calibrateDrone(Drone drone) {
        drone.getEngineRotorPairs().stream()
                .filter(it -> !it.isCalibrated())
                .forEach(this::calibrateEngineRotorPair);

        drone.calibrate(robotId, getCalibrationSumFor(drone));
        OperationUtils.simulateDelay(1000);
    }

    private int getCalibrationSumFor(Drone drone) {
        return drone.getEngineRotorPairs().stream().mapToInt(EngineRotorPair::getCalibrationValue).sum();
    }

    public void stop() {
        log.info("Stopping assembly robot {}", robotId);

        keepWorking.set(false);
    }

}

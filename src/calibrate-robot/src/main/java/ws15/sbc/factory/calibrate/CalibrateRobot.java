package ws15.sbc.factory.calibrate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.CalibratedDroneRepository;
import ws15.sbc.factory.common.repository.DroneRepository;
import ws15.sbc.factory.common.repository.ProcessedComponentRepository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.common.utils.OperationUtils;
import ws15.sbc.factory.dto.Drone;
import ws15.sbc.factory.dto.EngineRotorPair;
import ws15.sbc.factory.dto.UnCalibratedEngineRotorPair;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class CalibrateRobot {

    private static final Logger log = LoggerFactory.getLogger(CalibrateRobot.class);

    private static final Random random = new Random();

    private final String robotId;
    private final AtomicBoolean keepWorking = new AtomicBoolean(true);

    @Inject
    private ProcessedComponentRepository processedComponentRepo;

    @Inject
    private DroneRepository droneRepo;

    @Inject
    private CalibratedDroneRepository calibratedDroneRepo;

    @Inject
    private TxManager txManager;

    public CalibrateRobot() {
        robotId = UUID.randomUUID().toString();
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

        Optional<UnCalibratedEngineRotorPair> opEngineRotorPair = processedComponentRepo.takeOne(UnCalibratedEngineRotorPair.class);
        if (opEngineRotorPair.isPresent()) {
            UnCalibratedEngineRotorPair uncalibrated = opEngineRotorPair.get();

            EngineRotorPair calibrated = uncalibrated.calibrate();
            OperationUtils.simulateDelay(1000);

            processedComponentRepo.storeEntity(calibrated);
            log.info("Engine-rotor pair have been calibrated and stored");
        } else {
            log.info("No engine-rotor pair found");
        }

        txManager.commit();
    }

    private void takeDrone() {
        log.info("Looking for a drone...");
        txManager.beginTransaction();

        Optional<Drone> opDrone = droneRepo.takeOne(Drone.class);
        if (opDrone.isPresent()) {
            Drone drone = opDrone.get();
            calibrateDrone(drone);
            calibratedDroneRepo.storeEntity(drone);
            log.info("Drone have been calibrated and stored");
        } else {
            log.info("No drone found");
        }

        txManager.commit();
    }

    private void calibrateDrone(Drone drone) {
        List<EngineRotorPair> engineRotors = drone.getEngineRotorPairs().stream().map(e -> {
            if (e.isCalibrated()) {
                return e;
            } else {
                OperationUtils.simulateDelay(1000);
                return e.calibrate();
            }
        }).collect(Collectors.toList());

        drone.setEngineRotorPairs(engineRotors);
    }

    public void stop() {
        log.info("Stopping assembly robot {}", robotId);

        keepWorking.set(false);
    }

}

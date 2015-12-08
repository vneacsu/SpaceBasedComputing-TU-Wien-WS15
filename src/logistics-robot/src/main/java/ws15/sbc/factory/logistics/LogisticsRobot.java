package ws15.sbc.factory.logistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.CalibratedDrone;
import ws15.sbc.factory.common.dto.Drone;
import ws15.sbc.factory.common.repository.BadDroneRepository;
import ws15.sbc.factory.common.repository.CalibratedDroneRepository;
import ws15.sbc.factory.common.repository.GoodDroneRepository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.common.utils.OperationUtils;
import ws15.sbc.factory.common.utils.PropertyUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogisticsRobot {

    private static final Logger log = LoggerFactory.getLogger(LogisticsRobot.class);

    private final String robotId;
    private final AtomicBoolean keepWorking = new AtomicBoolean(true);

    @Inject @Named("AdmissibleRange")
    private Integer admissibleRange;

    @Inject private CalibratedDroneRepository calibratedDroneRepo;
    @Inject private GoodDroneRepository goodDroneRepo;
    @Inject private BadDroneRepository badDroneRepo;
    @Inject private TxManager txManager;

    public LogisticsRobot() {
        this.robotId = PropertyUtils.getProperty("robotId").orElse(UUID.randomUUID().toString());
    }

    public void run() {
        while (keepWorking.get()) {
            log.info("Trying to get a drone...");
            txManager.beginTransaction();


            Optional<CalibratedDrone> opDrone = calibratedDroneRepo.takeOne(CalibratedDrone.class);
            if (opDrone.isPresent()) {
                Drone drone = opDrone.get();

                log.info("Found drone with calibration value {}", drone.getCalibrationSum());

                drone.setTestedBy(robotId);
                if (isInAdmissibleRange(drone)) {
                    goodDroneRepo.storeEntity(drone.toGoodDrone());
                    log.info("Drone has been moved to the logistics container");
                } else {
                    badDroneRepo.storeEntity(drone.toBadDrone());
                    log.info("Drone has been moved to the refuse container");
                }

                OperationUtils.simulateDelay(1000);
            } else {
                log.info("No drone found");
            }

            txManager.commit();
        }
    }

    private boolean isInAdmissibleRange(Drone drone) {
        return Math.abs(drone.getCalibrationSum()) <= admissibleRange;
    }

    public void stop() {
        log.info("Stopping assembly robot {}", robotId);

        keepWorking.set(false);
    }

}

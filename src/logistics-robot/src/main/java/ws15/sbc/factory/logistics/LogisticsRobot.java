package ws15.sbc.factory.logistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.Drone;
import ws15.sbc.factory.common.repository.EntityMatcher;
import ws15.sbc.factory.common.repository.Repository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.common.utils.OperationUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static ws15.sbc.factory.common.dto.Drone.IS_CALIBRATED_PATH;
import static ws15.sbc.factory.common.dto.Drone.TESTED_BY_FIELD;

public class LogisticsRobot {

    private static final Logger log = LoggerFactory.getLogger(LogisticsRobot.class);

    private final AtomicBoolean keepWorking = new AtomicBoolean(true);

    @Inject @Named("AdmissibleRange")
    private Integer admissibleRange;

    @Inject
    @Named("robotId")
    private String robotId;

    @Inject
    private Repository repository;
    @Inject private TxManager txManager;

    public void run() {
        while (keepWorking.get()) {
            log.info("Trying to get a drone...");
            txManager.beginTransaction();


            Optional<Drone> opDrone = repository.takeOne(calibratedNotTestedDroneMatcher());
            if (opDrone.isPresent()) {
                Drone drone = opDrone.get();
                log.info("Found drone with calibration value {}", drone.getCalibrationSum());

                drone.setTestResult(robotId, isInAdmissibleRange(drone));
                OperationUtils.simulateDelay(1000);

                repository.storeEntity(drone);
            } else {
                log.info("No drone found");
            }

            txManager.commit();
        }
    }

    private EntityMatcher<Drone> calibratedNotTestedDroneMatcher() {
        return EntityMatcher.of(Drone.class)
                .withNotNullField(IS_CALIBRATED_PATH)
                .withNullField(TESTED_BY_FIELD);
    }

    private boolean isInAdmissibleRange(Drone drone) {
        return Math.abs(drone.getCalibrationSum()) <= admissibleRange;
    }

    public void stop() {
        log.info("Stopping assembly robot {}", robotId);

        keepWorking.set(false);
    }

}

package ws15.sbc.factory.logistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.Contract;
import ws15.sbc.factory.common.dto.Drone;
import ws15.sbc.factory.common.repository.EntityMatcher;
import ws15.sbc.factory.common.repository.Repository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.common.utils.OperationUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static ws15.sbc.factory.common.dto.Contract.IS_COMPLETED_FIELD;
import static ws15.sbc.factory.common.dto.Drone.*;

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
    @Inject
    private TxManager txManager;

    public void run() {
        while (keepWorking.get()) {
            performQualityCheckOnNextUntestedDrone();

            performContractDelivery();
        }
    }

    private void performQualityCheckOnNextUntestedDrone() {
        txManager.beginTransaction();

        Optional<Drone> opDrone = acquireUntestedDrone();
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

    private Optional<Drone> acquireUntestedDrone() {
        log.info("Trying to get an untested drone...");

        EntityMatcher<Drone> matcher = EntityMatcher.of(Drone.class)
                .withNotNullField(IS_CALIBRATED_PATH)
                .withNullField(TESTED_BY_FIELD);

        return repository.takeOne(matcher);
    }

    private boolean isInAdmissibleRange(Drone drone) {
        return Math.abs(drone.getCalibrationSum()) <= admissibleRange;
    }

    private void performContractDelivery() {
        txManager.beginTransaction();

        List<Drone> freeOfContractDrones = takeFreeOfContractDrones();
        List<Contract> contracts = takeUnfinishedContracts();

        assignDronesToContracts(freeOfContractDrones, contracts);

        repository.storeEntities(freeOfContractDrones);
        repository.storeEntities(contracts);

        txManager.commit();
    }

    private void assignDronesToContracts(List<Drone> freeOfContractDrones, List<Contract> contracts) {
        freeOfContractDrones.forEach(drone -> contracts.stream()
                .filter(it -> !it.isCompleted() && it.acceptsDrone(drone))
                .findFirst()
                .ifPresent(it -> it.assign(drone))
        );
    }

    private List<Drone> takeFreeOfContractDrones() {
        log.info("Taking all free of contract drones");

        EntityMatcher<Drone> matcher = EntityMatcher.of(Drone.class)
                .withFieldEqualTo(IS_GOOD_DRONE_FIELD, true)
                .withNullField(FOR_CONTRACT_FIELD);

        return repository.takeAll(matcher);
    }

    private List<Contract> takeUnfinishedContracts() {
        log.info("Taking all unfinished contracts");

        EntityMatcher<Contract> matcher = EntityMatcher.of(Contract.class)
                .withFieldEqualTo(IS_COMPLETED_FIELD, false);

        return repository.takeAll(matcher);
    }

    public void stop() {
        log.info("Stopping assembly robot {}", robotId);

        keepWorking.set(false);
    }

}

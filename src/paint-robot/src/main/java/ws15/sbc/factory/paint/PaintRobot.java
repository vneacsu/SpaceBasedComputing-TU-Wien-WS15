package ws15.sbc.factory.paint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.Casing;
import ws15.sbc.factory.common.dto.Contract;
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

public class PaintRobot {

    private static final Logger log = LoggerFactory.getLogger(PaintRobot.class);

    @Inject
    @Named("RobotId")
    private String robotId;
    @Inject
    private TxManager txManager;
    @Inject
    private Repository repository;

    private final AtomicBoolean keepWorking = new AtomicBoolean(true);

    public static final EntityMatcher<Casing> GRAY_CASING_MATCHER = EntityMatcher.of(Casing.class)
            .withFieldEqualTo(Casing.COLOR_FIELD, Casing.Color.GRAY);

    public void run() {
        log.info("Starting paint robot <{}> started work", robotId);

        while (keepWorking.get()) {
            txManager.beginTransaction();

            repository.takeOne(GRAY_CASING_MATCHER).ifPresent(casing -> {
                Optional<Contract> firstOpenContract = repository.getFirstOpenContract();
                if (firstOpenContract.isPresent()) {
                    Casing.Color requiredColor = firstOpenContract.get().getCasingColor();
                    casing.setColor(requiredColor);
                } else {
                    casing.setRandomColorNoGray();
                }
                OperationUtils.simulateDelay(1000);

                repository.storeEntity(casing);
            });

            txManager.commit();
        }

        log.info("Paint robot <{}> finished work", robotId);
    }

    public void stop() {
        log.info("Stopping paint robot {}", robotId);

        keepWorking.set(false);
    }

}

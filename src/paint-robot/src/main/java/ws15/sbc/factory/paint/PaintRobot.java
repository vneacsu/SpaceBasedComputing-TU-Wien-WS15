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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

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
            Optional<Contract> firstOpenContract = repository.getFirstOpenContract();

            txManager.beginTransaction();

            if (firstOpenContract.isPresent()) {
                EntityMatcher<Casing> matcher = EntityMatcher.of(Casing.class)
                        .withFieldEqualTo(Casing.COLOR_FIELD, Casing.Color.GRAY)
                        .withFieldEqualTo(Casing.TYPE_FIELD, firstOpenContract.get().getCasingType());

                repository.takeOne(matcher).ifPresent(casing -> {
                    casing.setColor(firstOpenContract.get().getCasingColor());
                    OperationUtils.simulateDelay(1000);

                    repository.storeEntity(casing);
                });
            } else {
                repository.takeOne(GRAY_CASING_MATCHER).ifPresent(casing -> {
                    casing.setRandomColorNoGray();
                    OperationUtils.simulateDelay(1000);

                    repository.storeEntity(casing);
                });
            }

            txManager.commit();
        }

        log.info("Paint robot <{}> finished work", robotId);
    }

    public void stop() {
        log.info("Stopping paint robot {}", robotId);

        keepWorking.set(false);
    }

}

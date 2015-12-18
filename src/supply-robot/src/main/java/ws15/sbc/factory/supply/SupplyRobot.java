package ws15.sbc.factory.supply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.RawComponent;
import ws15.sbc.factory.common.dto.factory.RawComponentFactory;
import ws15.sbc.factory.common.repository.Repository;
import ws15.sbc.factory.common.utils.OperationUtils;

public class SupplyRobot {

    final static Logger LOG = LoggerFactory.getLogger(SupplyRobot.class);

    private final String robotId;
    private final Repository repository;
    private final RawComponentFactory rawComponentFactory;
    private final Integer quantity;
    private final Long interval;

    public SupplyRobot(String robotId, Repository repository,
                       RawComponentFactory rawComponentFactory,
                       Integer quantity, Long interval) {
        this.robotId = robotId;
        this.repository = repository;
        this.rawComponentFactory = rawComponentFactory;
        this.quantity = quantity;
        this.interval = interval;

        LOG.info("{} robot created", this.robotId);
    }

    public void run() {
        LOG.info("{} robot is working hard", robotId);

        for (int i = 0; i < quantity; i++) {
            RawComponent rawComponent = rawComponentFactory.produceRawComponent(robotId);
            OperationUtils.simulateDelay(interval);

            repository.storeEntity(rawComponent);

            LOG.info("{} delivered by robot {}", rawComponent, robotId);
        }

        LOG.info("{} robot finished", robotId);
    }

}

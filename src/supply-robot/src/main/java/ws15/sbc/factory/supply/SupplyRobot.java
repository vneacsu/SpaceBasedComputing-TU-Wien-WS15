package ws15.sbc.factory.supply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.RawComponent;
import ws15.sbc.factory.common.dto.factory.RawComponentFactory;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.common.repository.mzs.SpaceBasedRawComponentRepository;

public class SupplyRobot {

    final static Logger LOG = LoggerFactory.getLogger(SpaceBasedRawComponentRepository.class);

    private final String robotId;
    private final RawComponentRepository rawComponentRepository;
    private final RawComponentFactory rawComponentFactory;
    private final Integer quantity;
    private final Long interval;

    public SupplyRobot(String robotId, RawComponentRepository rawComponentRepository,
                       RawComponentFactory rawComponentFactory,
                       Integer quantity,
                       Long interval) {
        this.robotId = robotId;
        this.rawComponentRepository = rawComponentRepository;
        this.rawComponentFactory = rawComponentFactory;
        this.quantity = quantity;
        this.interval = interval;

        LOG.info("{} robot created", this.robotId);
    }

    public void run() {
        LOG.info("{} robot is working hard", robotId);

        //noinspection InfiniteLoopStatement
        for (int i = 0; i < quantity; i++) {
            RawComponent rawComponent = rawComponentFactory.produceRawComponent(robotId);
            rawComponentRepository.storeEntity(rawComponent);
            LOG.info("{} delivered by robot {}", rawComponent, robotId);

            try { Thread.sleep(interval); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        LOG.info("{} robot goes to sleep", robotId);
    }

}

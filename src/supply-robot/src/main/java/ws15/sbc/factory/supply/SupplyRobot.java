package ws15.sbc.factory.supply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.common.repository.mzs.SpaceBasedRawComponentRepository;
import ws15.sbc.factory.dto.RawComponent;
import ws15.sbc.factory.dto.factory.RawComponentFactory;

import java.util.UUID;

public class SupplyRobot {

    final static Logger LOG = LoggerFactory.getLogger(SpaceBasedRawComponentRepository.class);

    private final String id;
    private final RawComponentRepository rawComponentRepository;
    private final RawComponentFactory rawComponentFactory;
    private final Integer quantity;
    private final Long interval;

    public SupplyRobot(RawComponentRepository rawComponentRepository,
                       RawComponentFactory rawComponentFactory,
                       Integer quantity,
                       Long interval) {
        this.rawComponentRepository = rawComponentRepository;
        this.rawComponentFactory = rawComponentFactory;
        this.quantity = quantity;
        this.interval = interval;

        id = UUID.randomUUID().toString();
        LOG.info("{} robot created", id);
    }

    public void run() {
        LOG.info("{} robot is working hard", id);

        //noinspection InfiniteLoopStatement
        for (int i = 0; i < quantity; i++) {
            RawComponent rawComponent = rawComponentFactory.produceRawComponent();
            rawComponentRepository.storeEntity(rawComponent);
            LOG.info("{} delivered by robot {}", rawComponent, id);

            try { Thread.sleep(interval); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        LOG.info("{} robot goes to sleep", id);
    }

}

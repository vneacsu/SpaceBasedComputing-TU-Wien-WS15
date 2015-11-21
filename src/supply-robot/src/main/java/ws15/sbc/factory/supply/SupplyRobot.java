package ws15.sbc.factory.supply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.ComponentRepository;
import ws15.sbc.factory.common.repository.SpaceBasedComponentRepository;
import ws15.sbc.factory.dto.Component;
import ws15.sbc.factory.dto.factory.ComponentFactory;

import java.util.UUID;

public class SupplyRobot {

    final static Logger LOG = LoggerFactory.getLogger(SpaceBasedComponentRepository.class);

    private final String id;
    private final ComponentRepository componentRepository;
    private final ComponentFactory componentFactory;
    private final Integer quantity;
    private final Long interval;

    public SupplyRobot(ComponentRepository componentRepository,
                       ComponentFactory componentFactory,
                       Integer quantity,
                       Long interval) {
        this.componentRepository = componentRepository;
        this.componentFactory = componentFactory;
        this.quantity = quantity;
        this.interval = interval;

        id = UUID.randomUUID().toString();
        LOG.info("{} robot created", id);
    }

    public void run() {
        LOG.info("{} robot is working hard", id);

        //noinspection InfiniteLoopStatement
        for (int i = 0; i < quantity; i++) {
            Component component = componentFactory.produceComponent();
            componentRepository.write(component);
            LOG.info("{} delivered by robot {}", component, id);

            try { Thread.sleep(interval); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        componentRepository.close();
        LOG.info("{} robot goes to sleep", id);
    }

}

package ws15.sbc.factory.common.repository.xbased;

import com.rabbitmq.client.Channel;
import ws15.sbc.factory.common.dto.GoodDrone;
import ws15.sbc.factory.common.repository.GoodDroneRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

@Singleton
public class XBasedGoodDroneRepository extends GenericXBasedRepository<GoodDrone> implements GoodDroneRepository {

    @Inject
    public XBasedGoodDroneRepository(Channel channel) {
        super(channel, "good-drones");
    }

    @Override
    public List<Class<? extends GoodDrone>> getQueueTypes() {
        return Collections.singletonList(GoodDrone.class);
    }
}

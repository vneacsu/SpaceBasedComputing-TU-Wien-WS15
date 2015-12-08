package ws15.sbc.factory.common.repository.xbased;

import com.google.inject.Inject;
import com.rabbitmq.client.Channel;
import ws15.sbc.factory.common.dto.BadDrone;
import ws15.sbc.factory.common.repository.BadDroneRepository;

import java.util.Collections;
import java.util.List;

public class XBasedBadDroneRepository extends GenericXBasedRepository<BadDrone> implements BadDroneRepository {

    @Inject
    public XBasedBadDroneRepository(Channel channel) {
        super(channel, "bad-drones");
    }

    @Override
    public List<Class<? extends BadDrone>> getQueuesType() {
        return Collections.singletonList(BadDrone.class);
    }
}

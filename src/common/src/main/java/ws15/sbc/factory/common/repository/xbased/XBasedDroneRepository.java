package ws15.sbc.factory.common.repository.xbased;

import com.rabbitmq.client.Channel;
import ws15.sbc.factory.common.dto.Drone;
import ws15.sbc.factory.common.repository.DroneRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

@Singleton
public class XBasedDroneRepository extends GenericXBasedRepository<Drone> implements DroneRepository {

    @Inject
    public XBasedDroneRepository(Channel channel) {
        super(channel, "drones");
    }

    @Override
    public List<Class<? extends Drone>> getQueueTypes() {
        return Collections.singletonList(Drone.class);
    }
}

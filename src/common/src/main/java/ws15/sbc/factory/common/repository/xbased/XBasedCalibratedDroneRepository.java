package ws15.sbc.factory.common.repository.xbased;

import com.rabbitmq.client.Channel;
import ws15.sbc.factory.common.dto.CalibratedDrone;
import ws15.sbc.factory.common.repository.CalibratedDroneRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

@Singleton
public class XBasedCalibratedDroneRepository extends GenericXBasedRepository<CalibratedDrone> implements CalibratedDroneRepository {

    @Inject
    public XBasedCalibratedDroneRepository(Channel channel) {
        super(channel, "calibrated-drones");
    }

    @Override
    public List<Class<? extends CalibratedDrone>> getQueuesType() {
        return Collections.singletonList(CalibratedDrone.class);
    }
}

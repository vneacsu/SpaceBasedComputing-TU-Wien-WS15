package ws15.sbc.factory.common.repository.mzs;

import org.mozartspaces.core.Capi;
import org.mozartspaces.notifications.NotificationManager;
import ws15.sbc.factory.common.dto.Drone;
import ws15.sbc.factory.common.repository.DroneRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

@Singleton
public class SpaceBasedDroneRepository extends GenericSpaceBasedRepository<Drone> implements DroneRepository {

    @Inject
    public SpaceBasedDroneRepository(SpaceBasedTxManager txManager, Capi capi, NotificationManager notificationManager, URI space) {
        super(txManager, capi, notificationManager, space);
    }

    @Override
    protected String getContainerName() {
        return "drones";
    }
}

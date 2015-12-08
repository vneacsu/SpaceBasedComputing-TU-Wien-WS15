package ws15.sbc.factory.common.repository.mzs;

import org.mozartspaces.core.Capi;
import org.mozartspaces.notifications.NotificationManager;
import ws15.sbc.factory.common.dto.BadDrone;
import ws15.sbc.factory.common.repository.BadDroneRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

@Singleton
public class SpaceBasedBadDroneRepository extends GenericSpaceBasedRepository<BadDrone> implements BadDroneRepository {

    @Inject
    public SpaceBasedBadDroneRepository(SpaceBasedTxManager txManager,
                                        Capi capi,
                                        NotificationManager notificationManager,
                                        URI space) {
        super(txManager, capi, notificationManager, space);
    }

    @Override
    protected String getContainerName() {
        return "bad_drones";
    }

}

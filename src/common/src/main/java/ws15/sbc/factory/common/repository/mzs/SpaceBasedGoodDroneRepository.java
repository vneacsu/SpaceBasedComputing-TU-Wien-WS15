package ws15.sbc.factory.common.repository.mzs;

import org.mozartspaces.core.Capi;
import org.mozartspaces.notifications.NotificationManager;
import ws15.sbc.factory.common.dto.GoodDrone;
import ws15.sbc.factory.common.repository.GoodDroneRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

@Singleton
public class SpaceBasedGoodDroneRepository extends GenericSpaceBasedRepository<GoodDrone> implements GoodDroneRepository {

    @Inject
    public SpaceBasedGoodDroneRepository(SpaceBasedTxManager txManager,
                                         Capi capi,
                                         NotificationManager notificationManager,
                                         URI space) {
        super(txManager, capi, notificationManager, space);
    }

    @Override
    protected String getContainerName() {
        return "good_drones";
    }

}

package ws15.sbc.factory.common.repository.mzs;

import org.mozartspaces.core.Capi;
import org.mozartspaces.notifications.NotificationManager;
import ws15.sbc.factory.common.dto.RawComponent;
import ws15.sbc.factory.common.repository.RawComponentRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

@Singleton
public class SpaceBasedRawComponentRepository extends GenericSpaceBasedRepository<RawComponent> implements RawComponentRepository {

    @Inject
    public SpaceBasedRawComponentRepository(SpaceBasedTxManager txManager, Capi capi, NotificationManager notificationManager, URI space) {
        super(txManager, capi, notificationManager, space);
    }

    @Override
    protected String getContainerName() {
        return "raw-components";
    }
}

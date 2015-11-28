package ws15.sbc.factory.common.repository.mzs;

import org.mozartspaces.core.Capi;
import org.mozartspaces.notifications.NotificationManager;
import ws15.sbc.factory.common.dto.ProcessedComponent;
import ws15.sbc.factory.common.repository.ProcessedComponentRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

@Singleton
public class SpaceBasedProcessedComponentRepository extends GenericSpaceBasedRepository<ProcessedComponent> implements ProcessedComponentRepository {

    @Inject
    public SpaceBasedProcessedComponentRepository(SpaceBasedTxManager txManager, Capi capi, NotificationManager notificationManager, URI space) {
        super(txManager, capi, notificationManager, space);
    }

    @Override
    protected String getContainerName() {
        return "processed-components";
    }
}

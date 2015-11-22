package ws15.sbc.factory.common.repository.mzs;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.MzsCore;
import ws15.sbc.factory.common.repository.ProcessedComponentRepository;
import ws15.sbc.factory.dto.ProcessedComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

@Singleton
public class SpaceBasedProcessedComponentRepository extends BaseSpaceBasedRepository<ProcessedComponent> implements ProcessedComponentRepository {

    @Inject
    public SpaceBasedProcessedComponentRepository(SpaceBasedTxManager txManager, MzsCore core, Capi capi, URI space) {
        super(txManager, core, capi, space);
    }

    @Override
    protected String getContainerName() {
        return "processed-components";
    }
}

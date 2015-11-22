package ws15.sbc.factory.common.repository.mzs;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.MzsCore;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.dto.RawComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

@Singleton
public class SpaceBasedRawRawComponentRepository extends BaseSpaceBasedRepository<RawComponent> implements RawComponentRepository {

    @Inject
    public SpaceBasedRawRawComponentRepository(SpaceBasedTxManager txManager, MzsCore core, Capi capi, URI space) {
        super(txManager, core, capi, space);
    }

    @Override
    protected String getContainerName() {
        return "raw-components";
    }
}

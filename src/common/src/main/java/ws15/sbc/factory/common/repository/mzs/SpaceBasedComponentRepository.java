package ws15.sbc.factory.common.repository.mzs;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.MzsCore;
import ws15.sbc.factory.common.repository.ComponentRepository;
import ws15.sbc.factory.dto.Component;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

@Singleton
public class SpaceBasedComponentRepository extends BaseSpaceBasedRepository<Component> implements ComponentRepository {

    @Inject
    public SpaceBasedComponentRepository(SpaceBasedTxManager txManager, MzsCore core, Capi capi, URI space) {
        super(txManager, core, capi, space);
    }

    @Override
    protected String getContainerName() {
        return "components";
    }
}

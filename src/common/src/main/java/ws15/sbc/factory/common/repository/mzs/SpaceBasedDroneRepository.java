package ws15.sbc.factory.common.repository.mzs;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.MzsCore;
import ws15.sbc.factory.common.repository.DroneRepository;
import ws15.sbc.factory.dto.Drone;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

@Singleton
public class SpaceBasedDroneRepository extends BaseSpaceBasedRepository<Drone> implements DroneRepository {

    @Inject
    public SpaceBasedDroneRepository(SpaceBasedTxManager txManager, MzsCore core, Capi capi, URI space) {
        super(txManager, core, capi, space);
    }

    @Override
    protected String getContainerName() {
        return "drones";
    }
}

package ws15.sbc.factory.common.repository.xbased;

import ws15.sbc.factory.common.repository.DroneRepository;
import ws15.sbc.factory.dto.Drone;

import javax.inject.Singleton;

@Singleton
public class XBasedDroneRepository extends BaseXBasedRepository<Drone> implements DroneRepository {
}

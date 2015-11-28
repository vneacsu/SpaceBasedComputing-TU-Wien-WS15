package ws15.sbc.factory.common.repository.xbased;

import ws15.sbc.factory.common.dto.Drone;
import ws15.sbc.factory.common.repository.DroneRepository;

import javax.inject.Singleton;

@Singleton
public class XBasedDroneRepository extends GenericXBasedRepository<Drone> implements DroneRepository {
}

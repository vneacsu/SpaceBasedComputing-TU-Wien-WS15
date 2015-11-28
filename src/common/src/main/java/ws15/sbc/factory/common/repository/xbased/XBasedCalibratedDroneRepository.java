package ws15.sbc.factory.common.repository.xbased;

import ws15.sbc.factory.common.dto.Drone;
import ws15.sbc.factory.common.repository.CalibratedDroneRepository;

import javax.inject.Singleton;

@Singleton
public class XBasedCalibratedDroneRepository extends GenericXBasedRepository<Drone> implements CalibratedDroneRepository {
}

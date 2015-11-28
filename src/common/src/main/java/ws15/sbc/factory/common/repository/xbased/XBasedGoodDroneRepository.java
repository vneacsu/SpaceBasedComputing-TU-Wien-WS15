package ws15.sbc.factory.common.repository.xbased;

import ws15.sbc.factory.common.dto.Drone;
import ws15.sbc.factory.common.repository.GoodDroneRepository;

import javax.inject.Singleton;

@Singleton
public class XBasedGoodDroneRepository extends GenericXBasedRepository<Drone> implements GoodDroneRepository {
}

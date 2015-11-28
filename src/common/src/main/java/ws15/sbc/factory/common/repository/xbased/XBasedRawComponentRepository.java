package ws15.sbc.factory.common.repository.xbased;

import ws15.sbc.factory.common.dto.RawComponent;
import ws15.sbc.factory.common.repository.RawComponentRepository;

import javax.inject.Singleton;

@Singleton
public class XBasedRawComponentRepository extends GenericXBasedRepository<RawComponent> implements RawComponentRepository {
}

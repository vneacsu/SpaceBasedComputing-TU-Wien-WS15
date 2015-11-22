package ws15.sbc.factory.common.repository.xbased;

import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.dto.RawComponent;

import javax.inject.Singleton;

@Singleton
public class XBasedRawComponentRepository extends BaseXBasedRepository<RawComponent> implements RawComponentRepository {
}

package ws15.sbc.factory.common.repository.xbased;

import ws15.sbc.factory.common.dto.ProcessedComponent;
import ws15.sbc.factory.common.repository.ProcessedComponentRepository;

import javax.inject.Singleton;

@Singleton
public class XBasedProcessedComponentRepository extends GenericXBasedRepository<ProcessedComponent> implements ProcessedComponentRepository {
}

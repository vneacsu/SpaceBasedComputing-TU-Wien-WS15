package ws15.sbc.factory.common.repository.xbased;

import ws15.sbc.factory.common.repository.ProcessedComponentRepository;
import ws15.sbc.factory.dto.ProcessedComponent;

import javax.inject.Singleton;

@Singleton
public class XBasedProcessedComponentRepository extends GenericXBasedRepository<ProcessedComponent> implements ProcessedComponentRepository {
}

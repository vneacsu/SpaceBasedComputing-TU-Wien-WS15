package ws15.sbc.factory.common.repository.xbased;

import com.rabbitmq.client.Channel;
import ws15.sbc.factory.common.dto.*;
import ws15.sbc.factory.common.repository.ProcessedComponentRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

@Singleton
public class XBasedProcessedComponentRepository extends GenericXBasedRepository<ProcessedComponent> implements ProcessedComponentRepository {

    @Inject
    public XBasedProcessedComponentRepository(Channel channel) {
        super(channel, "processed-components");
    }

    @Override
    public List<Class<? extends ProcessedComponent>> getQueuesType() {
        return Arrays.asList(
                Carcase.class,
                EngineRotorPair.class,
                CalibratedEngineRotorPair.class,
                UnCalibratedEngineRotorPair.class
                );
    }
}

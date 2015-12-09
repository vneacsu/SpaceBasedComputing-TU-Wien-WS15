package ws15.sbc.factory.common.repository.xbased;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.*;
import ws15.sbc.factory.common.repository.RawComponentRepository;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class XBasedRawComponentRepository extends GenericXBasedRepository<RawComponent> implements RawComponentRepository {

    private static final Logger log = LoggerFactory.getLogger(XBasedRawComponentRepository.class);

    @Inject
    public XBasedRawComponentRepository(Channel channel) {
        super(channel, "raw-components");
    }

    @Override
    public List<Class<? extends RawComponent>> getQueueTypes() {
        return Arrays.asList(Engine.class, Rotor.class, Casing.class, ControlUnit.class);
    }

}

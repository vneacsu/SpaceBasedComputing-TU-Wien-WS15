package ws15.sbc.factory.supply.repository;

import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.ContainerUtil;

import java.io.Serializable;
import java.net.URI;

public class SpaceBasedComponentRepository implements ComponentRepository {

    final static Logger LOG = LoggerFactory.getLogger(SpaceBasedComponentRepository.class);

    private static final URI space = URI.create("xvsm://localhost:4242");

    private MzsCore core;
    private Capi capi;
    private ContainerReference cref;

    public SpaceBasedComponentRepository() {
        core = DefaultMzsCore.newInstance();
        capi = new Capi(core);
        try {
            cref = ContainerUtil.getOrCreateNamedContainer(space, "components", capi);
        } catch (MzsCoreException e) {
            LOG.error("Could not connect to container");
            throw new IllegalStateException("Could not connect to container", e);
        }

    }

    @Override
    public void write(Serializable serializable) {
        try {
            capi.write(new Entry(serializable), cref);
        } catch (MzsCoreException e) {
            LOG.error("Could not write to container");
            throw new IllegalStateException("Could not connect to container", e);
        }
    }

    @Override
    public void close() {
        core.shutdown(false);
    }

}

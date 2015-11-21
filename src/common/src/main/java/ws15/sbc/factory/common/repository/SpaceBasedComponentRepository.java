package ws15.sbc.factory.common.repository;

import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.dto.Component;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
            cref = getOrCreateNamedContainer(space, "components", capi);
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
    public List<Component> readAll() {
        return null; // TODO
    }

    @Override
    public void close() {
        core.shutdown(false);
    }

    private ContainerReference getOrCreateNamedContainer(final URI space,
                                                               final String containerName,
                                                               final Capi capi) throws MzsCoreException {

        ContainerReference cref;
        try {
            // Get the Container
            LOG.info("Lookup container");
            cref = capi.lookupContainer(containerName, space, MzsConstants.RequestTimeout.DEFAULT, null);
            LOG.info("Container found");
            // If it is unknown, create it
        } catch (MzsCoreException e) {
            LOG.info("Container not found, creating it ...");
            // Create the Container
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<>();
            obligatoryCoords.add(new FifoCoordinator());
            cref = capi.createContainer(containerName, space, MzsConstants.Container.UNBOUNDED, obligatoryCoords, null, null);
            LOG.info("Container created");
        }
        return cref;
    }

}

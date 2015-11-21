package ws15.sbc.factory.common.repository;

import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.dto.Component;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.mozartspaces.core.MzsConstants.*;

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
            throw new IllegalStateException("Could not connect to container", e);
        }

    }

    @Override
    public void write(Serializable serializable) {
        try {
            capi.write(new Entry(serializable), cref);
        } catch (MzsCoreException e) {
            throw new IllegalStateException("Could not connect to container", e);
        }
    }

    @Override
    public List<Component> readAll() {
        try {
            // TODO use transactions to block the container
            List<FifoCoordinator.FifoSelector> selectors = Collections.singletonList(FifoCoordinator.newSelector(Selecting.COUNT_ALL));
            return capi.read(this.cref, selectors, RequestTimeout.TRY_ONCE, null);
        } catch (MzsCoreException e) {
            throw new IllegalStateException("Could not read from container", e);
        }
    }

    @Override
    public void onComponent(Consumer<Component> consumer) {
        NotificationManager notifManager = new NotificationManager(core);

        NotificationListener notifListener = (source, operation, entries) -> {
            Entry entry = (Entry) CapiUtil.getSingleEntry(entries);
            Component component = (Component) entry.getValue();
            consumer.accept(component);
        };

        try {
            notifManager.createNotification(cref, notifListener, Operation.WRITE);
        } catch (MzsCoreException | InterruptedException e) {
            throw new IllegalStateException("Could not set onWrite notification", e);
        }
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
            cref = capi.lookupContainer(containerName, space, RequestTimeout.DEFAULT, null);
            LOG.info("Container found");
            // If it is unknown, create it
        } catch (MzsCoreException e) {
            LOG.info("Container not found, creating it ...");
            // Create the Container
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<>();
            obligatoryCoords.add(new FifoCoordinator());
            cref = capi.createContainer(containerName, space, Container.UNBOUNDED, obligatoryCoords, null, null);
            LOG.info("Container created");
        }
        return cref;
    }

}

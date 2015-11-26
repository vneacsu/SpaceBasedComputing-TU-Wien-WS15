package ws15.sbc.factory.common.repository.mzs;

import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.core.MzsConstants.Container;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsConstants.Selecting;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.EntitySpecification;
import ws15.sbc.factory.common.repository.Repository;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.mozartspaces.core.MzsConstants.RequestTimeout.DEFAULT;

public abstract class BaseSpaceBasedRepository<Entity extends Serializable> implements Repository<Entity> {

    private static final Logger log = LoggerFactory.getLogger(BaseSpaceBasedRepository.class);

    private static final int TAKE_TIMEOUT = 2 * 1000;

    private URI space;
    private MzsCore core;
    private Capi capi;
    private ContainerReference cref;

    private SpaceBasedTxManager txManager;

    public BaseSpaceBasedRepository(SpaceBasedTxManager txManager, MzsCore core, Capi capi, URI space) {
        this.space = space;
        this.txManager = txManager;

        this.core = core;
        this.capi = capi;
        cref = getOrCreateComponentContainer();
    }

    private ContainerReference getOrCreateComponentContainer() {
        return getComponentContainer().orElseGet(this::createComponentContainer);
    }

    private Optional<ContainerReference> getComponentContainer() {
        log.info("Lookup component container");

        try {
            ContainerReference cref = capi.lookupContainer(getContainerName(), space, DEFAULT, null);

            log.info("Components container found");

            return Optional.of(cref);
        } catch (MzsCoreException e) {
            log.info("Components container not found");
            return Optional.empty();
        }
    }

    protected abstract String getContainerName();


    private ContainerReference createComponentContainer() {
        log.info("Creating components container");

        List<Coordinator> coordinators = asList(new FifoCoordinator(), new TypeCoordinator());

        try {
            return capi.createContainer(getContainerName(), space, Container.UNBOUNDED, coordinators, null, null);
        } catch (MzsCoreException e) {
            throw new RuntimeException("Failed to create components container", e);
        }
    }

    @Override
    public void writeEntities(Entity... components) {
        Entry[] entries = asList(components).stream()
                .map(Entry::new)
                .collect(toList())
                .toArray(new Entry[components.length]);

        try {
            capi.write(cref, DEFAULT, txManager.currentTransaction(), entries);
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends Entity> List<T> takeEntities(EntitySpecification... entitySpecifications) {
        List<Selector> selectors = asList(entitySpecifications).stream()
                .map(spec -> TypeCoordinator.newSelector(spec.getClazz(), spec.getCount()))
                .collect(toList());

        try {
            return capi.take(cref, selectors, TAKE_TIMEOUT, txManager.currentTransaction());
        } catch (CountNotMetException | MzsTimeoutException e) {
            log.warn("Failed to take entities from space");
            return emptyList();
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Entity> readAll() {
        try {
            // TODO use transactions to block the container
            List<FifoCoordinator.FifoSelector> selectors = singletonList(FifoCoordinator.newSelector(Selecting.COUNT_ALL));
            return capi.read(this.cref, selectors, RequestTimeout.TRY_ONCE, null);
        } catch (MzsCoreException e) {
            throw new IllegalStateException("Could not read from container", e);
        }
    }

    @Override
    public void onComponent(Consumer<Entity> consumer) {
        NotificationManager notifManager = new NotificationManager(core);

        NotificationListener notifListener = (source, operation, entries) -> {
            Entry entry = (Entry) CapiUtil.getSingleEntry(entries);
            Entity component = (Entity) entry.getValue();
            consumer.accept(component);
        };

        try {
            notifManager.createNotification(cref, notifListener, Operation.WRITE);
        } catch (MzsCoreException | InterruptedException e) {
            throw new IllegalStateException("Could not set onWrite notification", e);
        }
    }
}

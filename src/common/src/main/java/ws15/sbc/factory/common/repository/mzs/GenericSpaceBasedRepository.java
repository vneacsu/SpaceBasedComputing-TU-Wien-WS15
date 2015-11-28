package ws15.sbc.factory.common.repository.mzs;

import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.core.MzsConstants.Container;
import org.mozartspaces.core.MzsConstants.Selecting;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.Repository;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.mozartspaces.core.MzsConstants.RequestTimeout.DEFAULT;
import static org.mozartspaces.core.MzsConstants.RequestTimeout.TRY_ONCE;

public abstract class GenericSpaceBasedRepository<Entity extends Serializable> implements Repository<Entity> {

    private static final Logger log = LoggerFactory.getLogger(GenericSpaceBasedRepository.class);

    private static final int TAKE_TIMEOUT = 2 * 1000;

    private URI space;
    private Capi capi;
    private NotificationManager notificationManager;
    private ContainerReference cref;

    private SpaceBasedTxManager txManager;

    public GenericSpaceBasedRepository(SpaceBasedTxManager txManager, Capi capi, NotificationManager notificationManager, URI space) {
        this.space = space;
        this.txManager = txManager;

        this.capi = capi;
        this.notificationManager = notificationManager;

        cref = getOrCreateComponentContainer();
    }

    private ContainerReference getOrCreateComponentContainer() {
        return getComponentContainer().orElseGet(this::createComponentContainer);
    }

    private Optional<ContainerReference> getComponentContainer() {
        log.info("Lookup container {}", getContainerName());

        try {
            ContainerReference cref = capi.lookupContainer(getContainerName(), space, DEFAULT, null);

            log.info("Container {} found", getContainerName());

            return Optional.of(cref);
        } catch (MzsCoreException e) {
            log.info("Container {} not found", getContainerName());
            return Optional.empty();
        }
    }

    protected abstract String getContainerName();


    private ContainerReference createComponentContainer() {
        log.info("Creating container {}", getContainerName());

        List<Coordinator> coordinators = asList(new AnyCoordinator(), new TypeCoordinator());

        try {
            return capi.createContainer(getContainerName(), space, Container.UNBOUNDED, coordinators, null, null);
        } catch (MzsCoreException e) {
            throw new RuntimeException("Failed to create container", e);
        }
    }

    @Override
    public void storeEntity(Entity entity) {
        storeEntities(singletonList(entity));
    }

    @Override
    public void storeEntities(List<? extends Entity> entities) {
        log.info("Writing entities {} to container {}", Arrays.toString(entities.toArray()), getContainerName());

        Entry[] entries = entities.stream()
                .map(Entry::new)
                .collect(toList())
                .toArray(new Entry[entities.size()]);

        try {
            capi.write(cref, DEFAULT, txManager.currentTransaction(), entries);
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends Entity> Optional<T> takeOne(Class<T> clazz) {
        log.info("Taking one entity of type {} from container {}", clazz, getContainerName());

        try {
            List<T> entities = capi.take(cref, TypeCoordinator.newSelector(clazz), TAKE_TIMEOUT, txManager.currentTransaction());

            return Optional.of(entities.get(0));
        } catch (CountNotMetException | MzsTimeoutException e) {
            log.warn("Failed to take entity from container");
            return Optional.empty();
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Entity> readAll() {
        log.info("Reading all entities from container {}", getContainerName());

        try {
            capi.lockContainer(cref, txManager.currentTransaction());

            Selector selector = AnyCoordinator.newSelector(Selecting.COUNT_ALL);

            return capi.read(cref, selector, TRY_ONCE, txManager.currentTransaction());
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEntityStored(Consumer<Entity> consumer) {
        NotificationListener notificationListener = (source, operation, entries) -> entries.stream()
                .map(e -> ((Entry) e).getValue())
                .forEach(e -> consumer.accept((Entity) e));

        try {
            notificationManager.createNotification(cref, notificationListener, Operation.WRITE);
        } catch (MzsCoreException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEntityTaken(Consumer<Entity> consumer) {
        NotificationListener notificationListener = (source, operation, entries) -> entries.stream()
                .forEach(e -> consumer.accept((Entity) e));

        try {
            notificationManager.createNotification(cref, notificationListener, Operation.TAKE);
        } catch (MzsCoreException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

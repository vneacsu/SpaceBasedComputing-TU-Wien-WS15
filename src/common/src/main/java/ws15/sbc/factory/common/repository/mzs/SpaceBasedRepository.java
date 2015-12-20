package ws15.sbc.factory.common.repository.mzs;

import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.repository.EntityMatcher;
import ws15.sbc.factory.common.repository.Repository;

import javax.inject.Inject;
import javax.inject.Singleton;
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
import static org.mozartspaces.core.MzsConstants.RequestTimeout.ZERO;

@Singleton
public class SpaceBasedRepository implements Repository {

    private static final Logger log = LoggerFactory.getLogger(SpaceBasedRepository.class);

    private static final String CONTAINER_NAME = "factory-container";
    private static final int TAKE_TIMEOUT = 2 * 1000;

    final private URI space;
    final private Capi capi;
    final private ContainerReference cref;
    final private NotificationManager notificationManager;
    final private SpaceBasedTxManager txManager;

    @Inject
    public SpaceBasedRepository(SpaceBasedTxManager txManager, Capi capi, NotificationManager notificationManager, URI space) {
        this.space = space;
        this.txManager = txManager;

        this.capi = capi;
        this.notificationManager = notificationManager;

        this.cref = getOrCreateComponentContainer();
    }

    private ContainerReference getOrCreateComponentContainer() {
        return getComponentContainer().orElseGet(this::createComponentContainer);
    }

    private Optional<ContainerReference> getComponentContainer() {
        log.info("Lookup container {}", CONTAINER_NAME);

        try {
            ContainerReference cref = capi.lookupContainer(CONTAINER_NAME, space, DEFAULT, null);

            log.info("Container {} found", CONTAINER_NAME);

            return Optional.of(cref);
        } catch (MzsCoreException e) {
            log.info("Container {} not found", CONTAINER_NAME);
            return Optional.empty();
        }
    }

    private ContainerReference createComponentContainer() {
        log.info("Creating container {}", CONTAINER_NAME);

        List<Coordinator> coordinators = asList(new QueryCoordinator(), new FifoCoordinator());

        try {
            return capi.createContainer(CONTAINER_NAME, space, MzsConstants.Container.UNBOUNDED, coordinators, null, null);
        } catch (MzsCoreException e) {
            throw new RuntimeException("Failed to create container", e);
        }
    }

    @Override
    public void storeEntity(Serializable entity) {
        storeEntities(singletonList(entity));
    }

    @Override
    public void storeEntities(List<? extends Serializable> entities) {
        log.info("Writing entities {} to container {}", Arrays.toString(entities.toArray()), CONTAINER_NAME);

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
    public <T extends Serializable> Optional<T> takeOne(EntityMatcher<T> matcher) {
        Optional<List<T>> entities = take(matcher, 1);

        return entities.isPresent() ? Optional.of(entities.get().get(0)) : Optional.empty();
    }

    @Override
    public <T extends Serializable> Optional<List<T>> take(EntityMatcher<T> matcher, int count) {
        log.info("Taking {} entities matching {} from container {}", count, matcher, CONTAINER_NAME);

        Selector selector = QueryCoordinator.newSelector(matcher.mapToMzsQuery(), count);

        try {
            List<T> entities = capi.take(cref, selector, TAKE_TIMEOUT, txManager.currentTransaction());

            return Optional.of(entities);
        } catch (CountNotMetException | MzsTimeoutException e) {
            log.warn("Failed to take entity from container");
            return Optional.empty();
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int count(EntityMatcher<? extends Serializable> matcher) {
        Selector selector = QueryCoordinator.newSelector(matcher.mapToMzsQuery(), Selector.COUNT_MAX);

        try {
            int nEntities = capi.test(cref, selector, ZERO, txManager.currentTransaction());

            log.info("Counted {} entities matching {}", nEntities, matcher);

            return nEntities;
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends Serializable> void onEntityStored(EntityMatcher<T> matcher, Consumer<T> consumer) {
        NotificationListener notificationListener = (source, operation, entries) -> entries.stream()
                .map(e -> ((Entry) e).getValue())
                .filter(matcher::matches)
                .forEach(e -> consumer.accept((T) e)); //guarded by matcher

        createNotificationFor(Operation.WRITE, notificationListener);
    }

    private void createNotificationFor(Operation operation, NotificationListener listener) {
        try {
            notificationManager.createNotification(cref, listener, operation);
        } catch (MzsCoreException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends Serializable> void onEntityTaken(EntityMatcher<T> matcher, Consumer<T> consumer) {
        @SuppressWarnings("guarded by matcher")
        NotificationListener notificationListener = (source, operation, entries) -> entries.stream()
                .filter(matcher::matches)
                .forEach(e -> consumer.accept((T) e));

        createNotificationFor(Operation.TAKE, notificationListener);
    }
}

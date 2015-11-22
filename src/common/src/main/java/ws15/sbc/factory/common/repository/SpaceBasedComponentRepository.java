package ws15.sbc.factory.common.repository;

import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.dto.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mozartspaces.core.MzsConstants.*;
import static org.mozartspaces.core.MzsConstants.RequestTimeout.ZERO;

public class SpaceBasedComponentRepository implements ComponentRepository {

    final static Logger LOG = LoggerFactory.getLogger(SpaceBasedComponentRepository.class);

    private static final URI SPACE = URI.create("xvsm://localhost:4242");
    private static final String CONTAINER_NAME = "components";

    private MzsCore core;
    private Capi capi;
    private ContainerReference cref;

    private ThreadLocal<TransactionReference> currentTransaction = new ThreadLocal<>();

    public SpaceBasedComponentRepository() {
        core = DefaultMzsCore.newInstance();
        capi = new Capi(core);
        cref = getOrCreateComponentContainer();
    }

    private ContainerReference getOrCreateComponentContainer() {
        return getComponentContainer().orElseGet(this::createComponentContainer);
    }

    private Optional<ContainerReference> getComponentContainer() {
        LOG.info("Lookup component container");

        try {
            ContainerReference cref = capi.lookupContainer(CONTAINER_NAME, SPACE, RequestTimeout.DEFAULT, null);

            LOG.info("Components container found");

            return Optional.of(cref);
        } catch (MzsCoreException e) {
            LOG.info("Components container not found");
            return Optional.empty();
        }
    }

    private ContainerReference createComponentContainer() {
        LOG.info("Creating components container");

        List<Coordinator> coordinators = asList(new FifoCoordinator(), new TypeCoordinator());

        try {
            return capi.createContainer(CONTAINER_NAME, SPACE, Container.UNBOUNDED, coordinators, null, null);
        } catch (MzsCoreException e) {
            throw new RuntimeException("Failed to create components container", e);
        }
    }

    @Override
    public void write(Component... components) {
        Entry[] entries = asList(components).stream()
                .map(Entry::new)
                .collect(Collectors.toList())
                .toArray(new Entry[components.length]);

        try {
            capi.write(cref, ZERO, currentTransaction.get(), entries);
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends Component> List<T> takeComponents(ComponentSpecification... componentSpecifications) {
        List<Selector> selectors = asList(componentSpecifications).stream()
                .map(spec -> TypeCoordinator.newSelector(spec.getClazz(), spec.getCount()))
                .collect(Collectors.toList());

        try {
            return capi.take(cref, selectors, ZERO, currentTransaction.get());
        } catch (CountNotMetException e) {
            return emptyList();
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Component> readAll() {
        try {
            // TODO use transactions to block the container
            List<FifoCoordinator.FifoSelector> selectors = singletonList(FifoCoordinator.newSelector(Selecting.COUNT_ALL));
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

    @Override
    public void beginTransaction() {
        try {
            TransactionReference tref = capi.createTransaction(RequestTimeout.DEFAULT, SPACE);

            currentTransaction.set(tref);
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        try {
            capi.commitTransaction(currentTransaction.get());

            currentTransaction.set(null);
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
        try {
            capi.rollbackTransaction(currentTransaction.get());

            currentTransaction.set(null);
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }
}

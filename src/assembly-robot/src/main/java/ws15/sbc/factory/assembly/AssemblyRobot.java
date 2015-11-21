package ws15.sbc.factory.assembly;

import org.mozartspaces.capi3.CountNotMetException;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.capi3.TypeCoordinator;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.ContainerUtil;
import ws15.sbc.factory.dto.Component;
import ws15.sbc.factory.dto.Engine;
import ws15.sbc.factory.dto.EngineRotorPair;
import ws15.sbc.factory.dto.Rotor;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mozartspaces.core.MzsConstants.RequestTimeout.ZERO;

public class AssemblyRobot {
    private static final Logger log = LoggerFactory.getLogger(AssemblyRobot.class);

    private static final int TIMEOUT_MS = 20 * 1000;
    private static final int N_ENGINE_ROBOT_PAIRS = 3;

    private final String robotId;
    private final URI space;

    private final MzsCore core;
    private final Capi capi;
    private final ContainerReference cref;
    private final List<EngineRotorPair> availableEngineRobotPairs = new ArrayList<>(N_ENGINE_ROBOT_PAIRS);
    private TransactionReference currentTransaction;

    public AssemblyRobot(String robotId, URI space) {
        this.robotId = robotId;
        this.space = space;

        core = DefaultMzsCore.newInstance();
        capi = new Capi(core);
        cref = ContainerUtil.getOrCreateComponentsContainer(space, capi);
    }

    public void run() {
        acquireOrAssembleEngineRobotPairs();
        acquireOrAssembleCarcase();

        assembleDrone();

        core.shutdown(true);
    }

    private void acquireOrAssembleEngineRobotPairs() {
        beginTransaction();

        for (int i = 0; i < N_ENGINE_ROBOT_PAIRS; ++i) {
            Optional<EngineRotorPair> engineRotorPair = acquireOrAssembleEngineRotorPair();

            if (!engineRotorPair.isPresent()) {
                storeEngineRotorPairsForFutureUse();
                break;
            } else {
                availableEngineRobotPairs.add(engineRotorPair.get());
            }
        }

        commitTransaction();
    }

    private void beginTransaction() {
        try {
            currentTransaction = capi.createTransaction(TIMEOUT_MS, space);
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<EngineRotorPair> acquireOrAssembleEngineRotorPair() {
        Optional<EngineRotorPair> engineRotorPair = acquireEngineRotorPair();

        if (!engineRotorPair.isPresent()) {
            engineRotorPair = assembleEngineRotorPair();
        }
        return engineRotorPair;
    }

    private Optional<EngineRotorPair> acquireEngineRotorPair() {
        List<EngineRotorPair> engineRotorPairs = takeFromSpace(getEngineRotorPairSelector());

        return engineRotorPairs.isEmpty() ? Optional.empty() : Optional.of(engineRotorPairs.get(0));
    }

    private List<Selector> getEngineRotorPairSelector() {
        return singletonList(TypeCoordinator.newSelector(EngineRotorPair.class));
    }

    private <R extends Serializable> List<R> takeFromSpace(List<Selector> selectors) {
        try {
            return capi.take(cref, selectors, ZERO, currentTransaction);
        } catch (CountNotMetException e) {
            return emptyList();
        } catch (MzsCoreException e) {
            rollbackTransaction();
            throw new RuntimeException(e);
        }
    }

    private void rollbackTransaction() {
        try {
            capi.rollbackTransaction(currentTransaction);
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<EngineRotorPair> assembleEngineRotorPair() {
        List<Component> components = takeFromSpace(getEngineAndRotorSelector());

        return components.isEmpty() ? Optional.empty() :
                Optional.of(new EngineRotorPair(robotId, (Engine) components.get(0), (Rotor) components.get(0)));
    }

    private List<Selector> getEngineAndRotorSelector() {
        return asList(
                TypeCoordinator.newSelector(Engine.class),
                TypeCoordinator.newSelector(Rotor.class)
        );
    }

    private void storeEngineRotorPairsForFutureUse() {
        Entry[] entries = availableEngineRobotPairs.stream()
                .map(Entry::new)
                .collect(Collectors.toList())
                .toArray(new Entry[availableEngineRobotPairs.size()]);

        try {
            capi.write(cref, ZERO, currentTransaction, entries);
        } catch (MzsCoreException e) {
            rollbackTransaction();
            throw new RuntimeException(e);
        }

        availableEngineRobotPairs.clear();
    }

    private void commitTransaction() {
        try {
            capi.commitTransaction(currentTransaction);
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    private void acquireOrAssembleCarcase() {

    }

    private void assembleDrone() {
    }
}

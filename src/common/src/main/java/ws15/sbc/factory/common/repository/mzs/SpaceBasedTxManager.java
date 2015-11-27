package ws15.sbc.factory.common.repository.mzs;

import com.google.common.base.Preconditions;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.MzsTimeoutException;
import org.mozartspaces.core.TransactionReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.app.impl.SpaceBasedAppManager;
import ws15.sbc.factory.common.repository.TxManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

@Singleton
public class SpaceBasedTxManager implements TxManager<TransactionReference> {

    private static final int TIMEOUT_MS = 120 * 1000;

    private static final Logger log = LoggerFactory.getLogger(SpaceBasedAppManager.class);

    @Inject
    private Capi capi;
    @Inject
    private URI space;

    private ThreadLocal<TransactionReference> currentTransaction = new ThreadLocal<>();

    @Override
    public void beginTransaction() {
        Preconditions.checkState(currentTransaction.get() == null, "Transaction already in progress!");

        log.info("Begin transaction for space {}", space);

        try {
            TransactionReference tref = capi.createTransaction(TIMEOUT_MS, space);

            currentTransaction.set(tref);
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        Preconditions.checkNotNull(currentTransaction.get(), "No transaction in progress!");

        log.info("Commit current transaction for space {}", space);

        try {
            capi.commitTransaction(currentTransaction.get());

            currentTransaction.set(null);
        } catch (MzsTimeoutException e) {
            log.warn("Transaction timed out! Changes were rolled back");
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
        Preconditions.checkNotNull(currentTransaction.get(), "No transaction in progress!");

        log.info("Rollback current transaction for space {}", space);

        try {
            capi.rollbackTransaction(currentTransaction.get());

            currentTransaction.set(null);
        } catch (MzsTimeoutException e) {
            log.warn("Transaction timed out! Changes were rolled back");
        } catch (MzsCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TransactionReference currentTransaction() {
        return currentTransaction.get();
    }
}

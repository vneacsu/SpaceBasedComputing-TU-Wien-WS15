package ws15.sbc.factory.common.repository.xbased;

import org.mozartspaces.core.TransactionReference;
import ws15.sbc.factory.common.repository.TxManager;

import javax.inject.Singleton;

@Singleton
public class XBasedTxManager implements TxManager<TransactionReference> {
    @Override
    public void beginTransaction() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void commit() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void rollback() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TransactionReference currentTransaction() {
        return null;
    }
}

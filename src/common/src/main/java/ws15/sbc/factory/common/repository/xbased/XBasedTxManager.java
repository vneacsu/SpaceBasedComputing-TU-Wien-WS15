package ws15.sbc.factory.common.repository.xbased;

import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import org.mozartspaces.core.TransactionReference;
import ws15.sbc.factory.common.repository.TxManager;

import javax.inject.Singleton;

@Singleton
public class XBasedTxManager implements TxManager<TransactionReference> {
    @Override
    public void beginTransaction() {
        throw new UnsupportedMediaException();
    }

    @Override
    public void commit() {
        throw new UnsupportedMediaException();
    }

    @Override
    public void rollback() {
        throw new UnsupportedMediaException();
    }

    @Override
    public TransactionReference currentTransaction() {
        return null;
    }
}

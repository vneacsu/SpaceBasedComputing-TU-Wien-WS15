package ws15.sbc.factory.common.repository.xBased;

import com.rabbitmq.client.Channel;
import ws15.sbc.factory.common.repository.TxManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class XBasedTxManager implements TxManager {

    private Channel channel;

    private ThreadLocal<Boolean> transactionActive = new ThreadLocal<>();

    @Inject
    public XBasedTxManager(Channel channel) {
        this.channel = channel;

        enableTransactionalBehavior();
    }

    private void enableTransactionalBehavior() {
        beginTransaction();
        commit();
    }

    @Override
    public void beginTransaction() {
        try {
            channel.txSelect();
            transactionActive.set(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTransactionActive() {
        return transactionActive.get();
    }

    @Override
    public void commit() {
        transactionActive.set(false);
        try {
            channel.txCommit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
        transactionActive.set(false);

        try {
            channel.txRollback();
            channel.basicRecover();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object currentTransaction() {
        return null;
    }
}

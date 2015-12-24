package ws15.sbc.factory.common.repository.xBased;

import com.google.common.base.Preconditions;
import com.rabbitmq.client.Channel;
import ws15.sbc.factory.common.repository.TxManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class XBasedTxManager implements TxManager {

    private final Channel channel;

    private ThreadLocal<Boolean> transactionActive = new ThreadLocal<>();

    private final List<Runnable> hooks = new ArrayList<>();

    @Inject
    public XBasedTxManager(Channel channel) {
        this.channel = channel;

        enableTransactionalBehavior();
    }

    private void enableTransactionalBehavior() {
        transactionActive.set(false);

        beginTransaction();
        commit();
    }

    @Override
    public void beginTransaction() {
        Preconditions.checkState(!isTransactionActive());

        try {
            channel.txSelect();
            transactionActive.set(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void registerTxHook(Runnable hook) {
        Preconditions.checkState(isTransactionActive());

        hooks.add(hook);
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
        } finally {
            consumeTxHooks();
        }
    }

    private synchronized void consumeTxHooks() {
        hooks.forEach(Runnable::run);

        hooks.clear();
    }

    @Override
    public void rollback() {
        transactionActive.set(false);

        try {
            channel.txRollback();
            channel.basicRecover();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            consumeTxHooks();
        }
    }

    @Override
    public Object currentTransaction() {
        return null;
    }
}

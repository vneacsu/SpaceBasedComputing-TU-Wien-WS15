package ws15.sbc.factory.common.repository.xbased;

import com.rabbitmq.client.Channel;
import ws15.sbc.factory.common.repository.TxManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class XBasedTxManager implements TxManager {

    @Inject
    private Channel channel;

    @Override
    public void beginTransaction() {
        try {
            channel.txSelect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        try {
            channel.txCommit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
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

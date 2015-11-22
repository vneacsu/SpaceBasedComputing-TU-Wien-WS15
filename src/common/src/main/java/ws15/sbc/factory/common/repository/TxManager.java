package ws15.sbc.factory.common.repository;

public interface TxManager<Transaction> {
    void beginTransaction();
    void commit();
    void rollback();

    Transaction currentTransaction();
}

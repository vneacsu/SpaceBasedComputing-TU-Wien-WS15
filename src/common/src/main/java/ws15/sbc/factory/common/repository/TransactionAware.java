package ws15.sbc.factory.common.repository;

public interface TransactionAware {
    void beginTransaction();

    void commit();

    void rollback();
}

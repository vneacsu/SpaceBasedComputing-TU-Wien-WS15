package ws15.sbc.factory.assembly.steps;

import ws15.sbc.factory.common.repository.TxManager;

import javax.inject.Inject;

public abstract class TransactionalAssemblyStep implements AssemblyStep {

    @Inject
    private TxManager txManager;

    @Override
    public void performStep() {
        txManager.beginTransaction();

        try {
            performStepWithinTransaction();
        } catch (Exception e) {
            txManager.rollback();
            throw e;
        }

        txManager.commit();
    }

    protected abstract void performStepWithinTransaction();
}

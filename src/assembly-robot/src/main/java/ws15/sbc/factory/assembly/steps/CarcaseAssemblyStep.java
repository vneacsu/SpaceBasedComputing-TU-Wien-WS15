package ws15.sbc.factory.assembly.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws15.sbc.factory.common.dto.Carcase;
import ws15.sbc.factory.common.dto.Casing;
import ws15.sbc.factory.common.dto.Contract;
import ws15.sbc.factory.common.dto.ControlUnit;
import ws15.sbc.factory.common.repository.EntityMatcher;
import ws15.sbc.factory.common.repository.Repository;
import ws15.sbc.factory.common.repository.TxManager;
import ws15.sbc.factory.common.utils.OperationUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class CarcaseAssemblyStep implements AssemblyStep {

    private static final Logger log = LoggerFactory.getLogger(CarcaseAssemblyStep.class);

    @Inject
    @Named("RobotId")
    private String robotId;
    @Inject
    private TxManager txManager;
    @Inject
    private Repository repository;

    @Override
    public void performStep() {
        log.info("Performing carcase assembly step");

        List<Contract> contracts = repository.readAll(EntityMatcher.of(Contract.class));

        if (carcaseAlreadyAvailable(contracts)) {
            log.info("Carcase already available");
            return;
        }

        for (Contract contract : contracts) {
            if (produceCarcaseForContract(Optional.of(contract))) {
                return;
            }
        }

        produceCarcaseForContract(Optional.empty());
    }

    private boolean produceCarcaseForContract(Optional<Contract> contract) {
        txManager.beginTransaction();

        Optional<Carcase> carcase = assembleNewCarcase(contract);

        if (carcase.isPresent()) {
            log.info("Carcase successfully assembled");
            repository.storeEntity(carcase.get());

            txManager.commit();
        } else {
            log.info("Carcase could have not been assembled");

            txManager.rollback();
        }

        return carcase.isPresent();
    }

    private boolean carcaseAlreadyAvailable(List<Contract> contracts) {
        for (Contract contract : contracts) {
            EntityMatcher<Carcase> matcher = EntityMatcher.of(Carcase.class)
                    .withFieldEqualTo(Casing.TYPE_FIELD, contract.getCasingType())
                    .withFieldEqualTo(Casing.COLOR_FIELD, contract.getCasingColor());

            if (repository.count(matcher) > 0) {
                return true;
            }
        }

        return repository.count(EntityMatcher.of(Carcase.class)) > 0;
    }

    private Optional<Carcase> assembleNewCarcase(Optional<Contract> contract) {
        log.info("Trying to assemble new carcase");

        Optional<Casing> casing = getCasing(contract);
        if (!casing.isPresent()) {
            return Optional.empty();
        }

        Optional<ControlUnit> controlUnit = repository.takeOne(EntityMatcher.of(ControlUnit.class));
        if (!controlUnit.isPresent()) {
            return Optional.empty();
        }

        OperationUtils.simulateDelay(1000);

        return Optional.of(new Carcase(robotId, casing.get(), controlUnit.get()));
    }

    private Optional<Casing> getCasing(Optional<Contract> contract) {
        EntityMatcher<Casing> matcher;

        if (contract.isPresent()) {
            matcher = EntityMatcher.of(Casing.class)
                    .withFieldEqualTo(Casing.COLOR_FIELD, contract.get().getCasingColor())
                    .withFieldEqualTo(Casing.TYPE_FIELD, contract.get().getCasingType());
        } else {
            matcher = EntityMatcher.of(Casing.class);
        }

        return repository.takeOne(matcher);
    }
}

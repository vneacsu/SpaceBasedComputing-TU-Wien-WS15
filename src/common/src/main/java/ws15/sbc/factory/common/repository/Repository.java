package ws15.sbc.factory.common.repository;

import ws15.sbc.factory.common.dto.Contract;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static ws15.sbc.factory.common.dto.Contract.IS_COMPLETED_FIELD;

public interface Repository {

    void storeEntity(Serializable entity);

    void storeEntities(List<? extends Serializable> entities);

    <T extends Serializable> Optional<T> takeOne(EntityMatcher<T> matcher);

    <T extends Serializable> Optional<List<T>> take(EntityMatcher<T> matcher, int count);

    <T extends Serializable> List<T> takeAll(EntityMatcher<T> matcher);

    <T extends Serializable> List<T> readAll(EntityMatcher<T> matcher);

    int count(EntityMatcher<? extends Serializable> matcher);

    <T extends Serializable> void onEntityStored(EntityMatcher<T> matcher, Consumer<T> consumer);

    <T extends Serializable> void onEntityTaken(EntityMatcher<T> matcher, Consumer<T> consumer);

    default Optional<Contract> getFirstOpenContract() {
        EntityMatcher<Contract> openContractMatcher = EntityMatcher.of(Contract.class)
                .withFieldEqualTo(IS_COMPLETED_FIELD, false);

        List<Contract> contracts = readAll(openContractMatcher);
        if (contracts.size() > 0) {
            return Optional.of(contracts.get(0));
        } else {
            return Optional.empty();
        }
    }
}

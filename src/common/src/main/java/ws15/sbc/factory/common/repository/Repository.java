package ws15.sbc.factory.common.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface Repository {

    void storeEntity(Serializable entity);

    void storeEntities(List<? extends Serializable> entities);

    <T extends Serializable> Optional<T> takeOne(EntityMatcher<T> matcher);

    <T extends Serializable> Optional<List<T>> take(EntityMatcher<T> matcher, int count);

    int count(EntityMatcher<? extends Serializable> matcher, int nMaxEntities);

    <T extends Serializable> void onEntityStored(EntityMatcher<T> matcher, Consumer<T> consumer);

    <T extends Serializable> void onEntityTaken(EntityMatcher<T> matcher, Consumer<T> consumer);
}

package ws15.sbc.factory.common.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface Repository<Entity extends Serializable> {

    void storeEntity(Entity entity);

    void storeEntities(List<? extends Entity> entities);

    <T extends Entity> Optional<T> takeOne(Class<T> clazz);

    void onEntityStored(Consumer<Entity> consumer);

    void onEntityTaken(Consumer<Entity> consumer);
}

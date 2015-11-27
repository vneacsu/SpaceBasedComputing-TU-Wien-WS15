package ws15.sbc.factory.common.repository.xbased;

import ws15.sbc.factory.common.repository.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class BaseXBasedRepository<Entity extends Serializable> implements Repository<Entity> {

    @Override
    public void storeEntity(Entity components) {
        throw new UnsupportedOperationException(); // follows later
    }

    @Override
    public void storeEntities(List<? extends Entity> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Entity> Optional<T> takeOne(Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Entity> readAll() {
        throw new UnsupportedOperationException(); // follows later
    }

    @Override
    public void onEntityStored(Consumer<Entity> consumer) {
        throw new UnsupportedOperationException(); // follows later
    }

    @Override
    public void onEntityTaken(Consumer<Entity> consumer) {
        throw new UnsupportedOperationException(); // follows later
    }
}

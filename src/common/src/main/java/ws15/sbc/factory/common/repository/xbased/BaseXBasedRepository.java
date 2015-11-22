package ws15.sbc.factory.common.repository.xbased;

import ws15.sbc.factory.common.repository.EntitySpecification;
import ws15.sbc.factory.common.repository.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

public abstract class BaseXBasedRepository<Entity extends Serializable> implements Repository<Entity> {

    @Override
    public void write(Entity... components) {
        throw new UnsupportedOperationException(); // follows later
    }

    @Override
    public <T extends Entity> List<T> takeComponents(EntitySpecification... entitySpecifications) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Entity> readAll() {
        throw new UnsupportedOperationException(); // follows later
    }

    @Override
    public void onComponent(Consumer<Entity> consumer) {
        throw new UnsupportedOperationException(); // follows later
    }
}

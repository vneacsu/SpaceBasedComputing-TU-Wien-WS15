package ws15.sbc.factory.common.repository;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

public interface Repository<Entity extends Serializable> {

    void write(Entity... entities);

    <T extends Entity> List<T> takeComponents(EntitySpecification... entitySpecifications);

    List<Entity> readAll();

    void onComponent(Consumer<Entity> consumer);

}

package ws15.sbc.factory.common.repository;

import ws15.sbc.factory.dto.Component;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

public interface ComponentRepository extends TransactionAware {

    void write(Serializable serializable);

    <T extends Serializable> List<T> takeComponents(ComponentSpecification... componentSpecifications);

    List<Component> readAll();

    void onComponent(Consumer<Component> consumer);

    void close();

}

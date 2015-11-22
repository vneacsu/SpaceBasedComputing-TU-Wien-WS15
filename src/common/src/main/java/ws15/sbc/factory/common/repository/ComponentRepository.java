package ws15.sbc.factory.common.repository;

import ws15.sbc.factory.dto.Component;

import java.util.List;
import java.util.function.Consumer;

public interface ComponentRepository extends TransactionAware {

    void write(Component... components);

    <T extends Component> List<T> takeComponents(ComponentSpecification... componentSpecifications);

    List<Component> readAll();

    void onComponent(Consumer<Component> consumer);

}

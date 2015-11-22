package ws15.sbc.factory.common.repository;

import ws15.sbc.factory.dto.Component;

import javax.inject.Singleton;
import java.util.List;
import java.util.function.Consumer;

@Singleton
public class XBasedComponentRepository implements ComponentRepository {

    @Override
    public void write(Component... components) {
        throw new UnsupportedOperationException(); // follows later
    }

    @Override
    public <T extends Component> List<T> takeComponents(EntitySpecification... entitySpecifications) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Component> readAll() {
        throw new UnsupportedOperationException(); // follows later
    }

    @Override
    public void onComponent(Consumer<Component> consumer) {
        throw new UnsupportedOperationException(); // follows later
    }
}

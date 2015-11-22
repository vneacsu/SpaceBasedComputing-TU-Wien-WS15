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
    public <T extends Component> List<T> takeComponents(ComponentSpecification... componentSpecifications) {
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

    @Override
    public void close() {
        throw new UnsupportedOperationException(); // follows later
    }

    @Override
    public void beginTransaction() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void commit() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void rollback() {
        throw new UnsupportedOperationException();
    }
}

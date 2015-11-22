package ws15.sbc.factory.common.repository.xbased;

import ws15.sbc.factory.common.repository.EntitySpecification;
import ws15.sbc.factory.common.repository.RawComponentRepository;
import ws15.sbc.factory.dto.RawComponent;

import javax.inject.Singleton;
import java.util.List;
import java.util.function.Consumer;

@Singleton
public class XBasedRawComponentRepository implements RawComponentRepository {

    @Override
    public void write(RawComponent... components) {
        throw new UnsupportedOperationException(); // follows later
    }

    @Override
    public <T extends RawComponent> List<T> takeComponents(EntitySpecification... entitySpecifications) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<RawComponent> readAll() {
        throw new UnsupportedOperationException(); // follows later
    }

    @Override
    public void onComponent(Consumer<RawComponent> consumer) {
        throw new UnsupportedOperationException(); // follows later
    }
}

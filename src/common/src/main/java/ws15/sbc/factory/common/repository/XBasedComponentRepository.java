package ws15.sbc.factory.common.repository;

import ws15.sbc.factory.dto.Component;

import java.io.Serializable;
import java.util.List;

public class XBasedComponentRepository implements ComponentRepository {

    @Override
    public void write(Serializable serializable) {
        throw new UnsupportedOperationException(); // follows later
    }

    @Override
    public List<Component> readAll() {
        throw new UnsupportedOperationException(); // follows later
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException(); // follows later
    }
}

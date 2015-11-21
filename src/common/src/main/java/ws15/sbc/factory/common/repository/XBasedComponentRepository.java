package ws15.sbc.factory.common.repository;

import java.io.Serializable;

public class XBasedComponentRepository implements ComponentRepository {

    @Override
    public void write(Serializable serializable) {
        throw new UnsupportedOperationException(); // follows later
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException(); // follows later
    }
}

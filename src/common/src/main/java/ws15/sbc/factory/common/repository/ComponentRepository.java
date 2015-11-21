package ws15.sbc.factory.common.repository;

import java.io.Serializable;

public interface ComponentRepository {

    void write(Serializable serializable);

    void close();

}

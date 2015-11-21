package ws15.sbc.factory.supply.repository;

import java.io.Serializable;

public interface ComponentRepository {

    void write(Serializable serializable);

    void close();

}

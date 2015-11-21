package ws15.sbc.factory.common.repository;

import ws15.sbc.factory.dto.Component;

import java.io.Serializable;
import java.util.List;

public interface ComponentRepository {

    void write(Serializable serializable);

    List<Component> readAll();

    void close();

}

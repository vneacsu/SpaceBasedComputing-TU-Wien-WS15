package ws15.sbc.factory.common.repository;

import ws15.sbc.factory.dto.Component;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

public interface ComponentRepository {

    void write(Serializable serializable);

    List<Component> readAll();

    void onComponent(Consumer<Component> consumer);

    void close();

}

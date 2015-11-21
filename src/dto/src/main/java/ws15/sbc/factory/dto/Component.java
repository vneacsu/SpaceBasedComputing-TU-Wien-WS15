package ws15.sbc.factory.dto;

import java.io.Serializable;
import java.util.UUID;

public abstract class Component implements Serializable {

    private final String id;

    public Component() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Component{" +
                "id='" + id + '\'' +
                '}';
    }
}

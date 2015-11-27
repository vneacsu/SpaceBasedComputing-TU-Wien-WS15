package ws15.sbc.factory.dto;

import java.io.Serializable;
import java.util.Objects;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Component)) return false;
        Component component = (Component) o;
        return Objects.equals(id, component.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package ws15.sbc.factory.dto;

import java.io.Serializable;
import java.util.UUID;

public class Engine implements Serializable {

    private final String id;

    public Engine() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }
}

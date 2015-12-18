package ws15.sbc.factory.common.repository.xBased;

import java.io.Serializable;

class Event<Entity extends Serializable> implements Serializable {

    private final Entity entity;
    private final ActionType actionType;

    public Event(Entity entity, ActionType actionType) {
        this.entity = entity;
        this.actionType = actionType;
    }

    public Entity getEntity() {
        return entity;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public enum ActionType implements Serializable {
        TAKEN,
        STORED
    }
}

package ws15.sbc.factory.common.dto;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Contract implements Serializable {

    public static final String IS_COMPLETED_FIELD = "completed";

    private final String id = UUID.randomUUID().toString();

    private final int nDrones;
    private final Casing.Type casingType;
    private final Casing.Color casingColor;

    private boolean completed = false;

    private final List<Drone> completedDrones = new ArrayList<>();

    public Contract(int nDrones, Casing.Type casingType, Casing.Color casingColor) {
        this.nDrones = nDrones;
        this.casingType = casingType;
        this.casingColor = casingColor;
    }

    public void assign(Drone drone) {
        Preconditions.checkArgument(acceptsDrone(drone));
        Preconditions.checkState(!isCompleted());

        completedDrones.add(drone);
        completed = completedDrones.size() == nDrones;

        drone.setForContract(id);
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean acceptsDrone(Drone drone) {
        return drone.isGoodDrone() &&
                drone.getCarcase().getCasing().getType() == casingType &&
                drone.getCarcase().getCasing().getColor() == casingColor;
    }

    public Casing.Color getCasingColor() {
        return casingColor;
    }

    public Casing.Type getCasingType() {
        return casingType;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("numberCompletedDrones", completedDrones.size() + " out of " + nDrones)
                .add("casingType", casingType)
                .add("casingColor", casingColor)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contract)) return false;
        Contract contract = (Contract) o;
        return Objects.equal(id, contract.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

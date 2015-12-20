package ws15.sbc.factory.common.dto;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.UUID;

public class Contract implements Serializable {

    private final String id = UUID.randomUUID().toString();

    private final int nDrones;
    private final Casing.Type casingType;
    private final Casing.Color casingColor;

    public Contract(int nDrones, Casing.Type casingType, Casing.Color casingColor) {
        this.nDrones = nDrones;
        this.casingType = casingType;
        this.casingColor = casingColor;
    }

    public String getId() {
        return id;
    }

    public int getnDrones() {
        return nDrones;
    }

    public Casing.Type getCasingType() {
        return casingType;
    }

    public Casing.Color getCasingColor() {
        return casingColor;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("nDrones", nDrones)
                .add("casingType", casingType)
                .add("casingColor", casingColor)
                .toString();
    }
}

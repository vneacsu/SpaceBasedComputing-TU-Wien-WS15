package ws15.sbc.factory.common.dto;

import com.google.common.base.Objects;

import java.util.Random;

public class Casing extends RawComponent {

    private static final Random random = new Random();

    private final Type type = Type.getRandom();
    private Color color = Color.GRAY;

    public Casing(String suppliedBy) {
        super(suppliedBy);
    }

    public Type getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("type", type)
                .add("color", color)
                .add("suppliedBy", getSuppliedBy())
                .toString();
    }

    public enum Type {
        NORMAL, TRANSPORT, CAMERA_HOLDER;

        public static Type getRandom() {
            return values()[random.nextInt(values().length)];
        }
    }

    public enum Color {
        GRAY, RED, GREEN, BLUE
    }
}

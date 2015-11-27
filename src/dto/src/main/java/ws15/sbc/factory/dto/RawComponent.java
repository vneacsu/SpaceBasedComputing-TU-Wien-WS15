package ws15.sbc.factory.dto;

public abstract class RawComponent extends Component {

    private final String suppliedBy;

    public RawComponent(String suppliedBy) {
        this.suppliedBy = suppliedBy;
    }

    @Override
    public String toString() {
        return String.format("[%s: id - %s, suppliedBy - %s]",
                this.getClass().getSimpleName(), getId(), suppliedBy);
    }
}

package ws15.sbc.factory.dto;

public abstract class ProcessedComponent extends Component {

    private final String assembledBy;

    public ProcessedComponent(String assembledBy) {
        this.assembledBy = assembledBy;
    }

    @Override
    public String toString() {
        return String.format("[%s: id - %s, assembledBy - %s]",
                this.getClass().getSimpleName(), getId(), assembledBy);
    }
}

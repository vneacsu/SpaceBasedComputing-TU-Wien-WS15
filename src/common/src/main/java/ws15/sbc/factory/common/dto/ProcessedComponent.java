package ws15.sbc.factory.common.dto;

public abstract class ProcessedComponent extends Component {

    private final String assembledBy;

    public ProcessedComponent(String assembledBy) {
        this.assembledBy = assembledBy;
    }

    public String getAssembledBy() {
        return assembledBy;
    }

    @Override
    public String toString() {
        return String.format("[%s: id - %s, assembledBy - %s]",
                this.getClass().getSimpleName(), getId(), assembledBy);
    }
}

package ws15.sbc.factory.common.dto;

public abstract class ProcessedComponent extends Component {

    public static final String CALIBRATED_BY_FIELD = "calibratedBy";

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
                getComponentName(), getId(), assembledBy);
    }

    protected String getComponentName() {
        return getClass().getSimpleName();
    }
}

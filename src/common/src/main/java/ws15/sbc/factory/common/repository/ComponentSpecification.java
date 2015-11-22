package ws15.sbc.factory.common.repository;

public class ComponentSpecification {
    private Class clazz;
    private int count;

    public ComponentSpecification(Class clazz, int count) {
        this.clazz = clazz;
        this.count = count;
    }

    public ComponentSpecification(Class clazz) {
        this(clazz, 1);
    }

    public Class getClazz() {
        return clazz;
    }

    public int getCount() {
        return count;
    }
}

package ws15.sbc.factory.common.repository;

public class EntitySpecification {
    private Class clazz;
    private int count;

    public EntitySpecification(Class clazz, int count) {
        this.clazz = clazz;
        this.count = count;
    }

    public EntitySpecification(Class clazz) {
        this(clazz, 1);
    }

    public Class getClazz() {
        return clazz;
    }

    public int getCount() {
        return count;
    }
}

package ws15.sbc.factory.common.repository;

import org.apache.commons.beanutils.PropertyUtils;
import org.mozartspaces.capi3.Matchmaker;
import org.mozartspaces.capi3.Matchmakers;
import org.mozartspaces.capi3.Property;
import org.mozartspaces.capi3.Query;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityMatcher<Entity extends Serializable> {

    final private Class<Entity> entityClass;
    final private Map<String, Object> fieldEqualRules = new HashMap<>();
    final private Map<String, Object> fieldNotEqualRules = new HashMap<>();

    public static <T extends Serializable> EntityMatcher<T> of(Class<T> entityClass) {
        return new EntityMatcher<>(entityClass);
    }

    private EntityMatcher(Class<Entity> entityClass) {
        this.entityClass = entityClass;
    }

    public EntityMatcher<Entity> withFieldEqualTo(String field, Object value) {
        fieldEqualRules.put(field, value);

        return this;
    }

    public EntityMatcher<Entity> withFieldNotEqualTo(String field, Object value) {
        fieldNotEqualRules.put(field, value);

        return this;
    }

    public EntityMatcher<Entity> withNullField(String field) {
        return withFieldEqualTo(field, null);
    }

    public EntityMatcher<Entity> withNotNullField(String field) {
        return withFieldNotEqualTo(field, null);
    }

    public Class<Entity> getEntityClass() {
        return entityClass;
    }

    public Query mapToMzsQuery() {
        return new Query().filter(Matchmakers.and(getMatchmakers()));
    }

    private Matchmaker[] getMatchmakers() {
        List<Matchmaker> matchmakers = new ArrayList<>();

        matchmakers.add(Property.forClass(entityClass).exists());

        matchmakers.addAll(fieldEqualRules.entrySet().stream()
                .map(it -> Property.forClass(entityClass, it.getKey()).equalTo(it.getValue()))
                .collect(Collectors.toList()));

        matchmakers.addAll(fieldNotEqualRules.entrySet().stream()
                .map(it -> Property.forClass(entityClass, it.getKey()).notEqualTo(it.getValue()))
                .collect(Collectors.toList()));

        return matchmakers.toArray(new Matchmaker[matchmakers.size()]);
    }

    public boolean matches(Serializable entity) {
        return itMatchesClass(entity) && itFulfilsFieldEqualRules(entity) && itFulfilsFieldNotEqualRules(entity);
    }

    private boolean itMatchesClass(Serializable entity) {
        return entityClass.isAssignableFrom(entity.getClass());
    }

    private boolean itFulfilsFieldEqualRules(Serializable entity) {
        return fieldEqualRules.entrySet().stream()
                .allMatch(it -> entityFieldMatches(entity, it.getKey(), it.getValue()));
    }

    private boolean entityFieldMatches(Serializable entity, String field, Object expectedValue) {
        Object actualValue;
        try {
            actualValue = PropertyUtils.getProperty(entity, field);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return false;
        }

        return actualValue == null ? expectedValue == null : actualValue.equals(expectedValue);
    }

    private boolean itFulfilsFieldNotEqualRules(Serializable entity) {
        return fieldNotEqualRules.entrySet().stream()
                .allMatch(it -> !entityFieldMatches(entity, it.getKey(), it.getValue()));
    }

    @Override
    public String toString() {
        return "EntityMatcher{" +
                "entityClass=" + entityClass +
                ", fieldEqualRules=" + fieldEqualRules +
                ", fieldNotEqualRules=" + fieldNotEqualRules +
                '}';
    }
}

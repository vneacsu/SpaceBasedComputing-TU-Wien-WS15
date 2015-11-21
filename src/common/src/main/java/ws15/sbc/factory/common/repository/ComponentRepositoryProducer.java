package ws15.sbc.factory.common.repository;

import java.util.Optional;

public class ComponentRepositoryProducer {

    private static ComponentRepository componentRepository;

    private final static Object lock = new Object();

    public static ComponentRepository getSingleton() {
        final String repoStrategy = getProperty("repoStrategy").orElse("spaceBased");

        if (componentRepository == null) {
            synchronized (lock) {
                if (componentRepository == null) {
                    switch (repoStrategy) {
                        case "spaceBased":
                            componentRepository = new SpaceBasedComponentRepository();
                            break;
                        case "xBased":
                            componentRepository = new XBasedComponentRepository();
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid repoStrategy");
                    }
                }
            }
        }

        return componentRepository;
    }

    private static Optional<String> getProperty(String s) {
        final String value = System.getProperty(s);
        if (value != null) {
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }

}

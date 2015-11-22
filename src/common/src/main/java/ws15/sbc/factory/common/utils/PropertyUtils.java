package ws15.sbc.factory.common.utils;

import java.util.Optional;

public class PropertyUtils {

    public static Optional<String> getProperty(String s) {
        final String value = System.getProperty(s);
        if (value != null) {
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }
}

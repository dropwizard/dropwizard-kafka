package io.dropwizard.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DropwizardKafkaUtils {

    private static final Logger log = LoggerFactory.getLogger(DropwizardKafkaUtils.class);

    private DropwizardKafkaUtils() {
        // should not instantiate
    }

    public static void validateStringIsValidSubClass(final String classString, final Class<?> parentClass) {
        final Class<?> actualClass;
        try {
            actualClass = Class.forName(classString);

        } catch (final ClassNotFoundException e) {
            log.error("No valid class found for string={}", classString);
            throw new RuntimeException(e);
        }

        if (!parentClass.isAssignableFrom(actualClass)) {
            log.error("class={} is not a subclass of parentClass={}", actualClass, parentClass);
            throw new IllegalStateException(String.format("Class for name=%s is not a child of parentClass=%s", actualClass, parentClass));
        }
    }
}

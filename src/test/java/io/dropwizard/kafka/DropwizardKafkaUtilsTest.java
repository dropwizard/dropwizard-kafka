package io.dropwizard.kafka;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DropwizardKafkaUtilsTest {
    @Test
    public void actualSubclassShouldBeCorrectlyValidated() {
        final String integerClassName = Integer.class.getName();

        DropwizardKafkaUtils.validateStringIsValidSubClass(integerClassName, Object.class);
    }

    @Test
    public void nonSubclassShouldFailValidations() {
        final String arrayListClassName = ArrayList.class.getName();

        assertThrows(IllegalStateException.class, () -> DropwizardKafkaUtils.validateStringIsValidSubClass(arrayListClassName, Map.class));
    }

    @Test
    public void classThatDoesNotExistShouldFailValidations() {
        final String fakeClassName = "blah.blah.blah.businesslogic.ObjectFactoryFactoryVisitor";

        assertThrows(RuntimeException.class, () -> DropwizardKafkaUtils.validateStringIsValidSubClass(fakeClassName, Object.class));
    }
}

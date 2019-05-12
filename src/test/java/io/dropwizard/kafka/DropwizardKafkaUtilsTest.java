package io.dropwizard.kafka;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

public class DropwizardKafkaUtilsTest {
    @Test
    public void actualSubclassShouldBeCorrectlyValidated() {
        final String integerClassName = Integer.class.getName();

        DropwizardKafkaUtils.validateStringIsValidSubClass(integerClassName, Object.class);
    }

    @Test(expected = IllegalStateException.class)
    public void nonSubclassShouldFailValidations() {
        final String arrayListClassName = ArrayList.class.getName();

        DropwizardKafkaUtils.validateStringIsValidSubClass(arrayListClassName, Map.class);
    }

    @Test(expected = RuntimeException.class)
    public void classThatDoesNotExistShouldFailValidations() {
        final String fakeClassName = "blah.blah.blah.businesslogic.ObjectFactoryFactoryVisitor";

        DropwizardKafkaUtils.validateStringIsValidSubClass(fakeClassName, Object.class);
    }
}

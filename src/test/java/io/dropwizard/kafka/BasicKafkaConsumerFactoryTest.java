package io.dropwizard.kafka;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.Test;

import java.io.File;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicKafkaConsumerFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<KafkaConsumerFactory> configFactory =
            new YamlConfigurationFactory<>(KafkaConsumerFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildABasicKafkaConsumer() throws Exception {
        final File yml = new File(Resources.getResource("yaml/basic-consumer.yaml").toURI());
        final KafkaConsumerFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(KafkaConsumerFactory.class);
        final LifecycleEnvironment lifecycle = new LifecycleEnvironment();
        final HealthCheckRegistry healthChecks = new HealthCheckRegistry();

        final Consumer consumer = factory.build(lifecycle, healthChecks, null, null);

        assertThat(consumer)
                .isNotNull();
    }

    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(BasicKafkaConsumerFactory.class);
    }
}

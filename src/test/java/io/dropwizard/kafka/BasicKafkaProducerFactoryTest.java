package io.dropwizard.kafka;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import org.apache.kafka.clients.producer.Producer;
import org.junit.Test;

import java.io.File;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicKafkaProducerFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<KafkaProducerFactory> configFactory =
            new YamlConfigurationFactory<>(KafkaProducerFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildABasicKafkaProducer() throws Exception {
        final File yml = new File(Resources.getResource("yaml/basic-producer.yaml").toURI());
        final KafkaProducerFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(KafkaProducerFactory.class);
        final LifecycleEnvironment lifecycle = new LifecycleEnvironment();
        final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();

        final Producer producer = factory.build(lifecycle, healthCheckRegistry, ImmutableList.of("testTopic"), null);
        assertThat(producer)
                .isNotNull();
    }

    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(BasicKafkaProducerFactory.class);
    }
}

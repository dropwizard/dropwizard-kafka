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
import java.util.Collections;
import java.util.TreeSet;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BasicKafkaConsumerFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<KafkaConsumerFactory> configFactory =
            new YamlConfigurationFactory<>(KafkaConsumerFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildABasicKafkaConsumer() throws Exception {
        final File yml = new File(Resources.getResource("yml/basic-consumer.yml").toURI());
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
    public void buildingMultipleConsumersShouldResultInOnlyASingleHealthCheckRegistered() throws Exception {
        final File yml = new File(Resources.getResource("yml/basic-consumer.yml").toURI());
        final KafkaConsumerFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(KafkaConsumerFactory.class);

        final LifecycleEnvironment lifecycle = new LifecycleEnvironment();
        final HealthCheckRegistry healthChecks = mock(HealthCheckRegistry.class);
        when(healthChecks.getNames())
                .thenReturn(new TreeSet<>())
                .thenReturn(new TreeSet<>(Collections.singleton(factory.getName())));

        final Consumer consumer1 = factory.build(lifecycle, healthChecks,null, null);
        final Consumer consumer2 = factory.build(lifecycle, healthChecks, null, null);

        assertThat(consumer1)
                .isNotNull();
        assertThat(consumer2)
                .isNotNull();

        verify(healthChecks, times(1)).register(eq(factory.getName()), any());
    }

    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(BasicKafkaConsumerFactory.class);
    }
}

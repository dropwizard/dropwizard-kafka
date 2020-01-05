package io.dropwizard.kafka;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicKafkaAdminClientFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final MetricRegistry metrics = new MetricRegistry();
    private final YamlConfigurationFactory<BasicKafkaAdminClientFactory> configFactory =
            new YamlConfigurationFactory<>(BasicKafkaAdminClientFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildABasicKafkaAdminClient() throws Exception {
        final File yml = new File(Resources.getResource("yaml/basic-admin-no-topics.yaml").toURI());
        final KafkaAdminClientFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(KafkaAdminClientFactory.class);
        final LifecycleEnvironment lifecycle = new LifecycleEnvironment(metrics);
        final HealthCheckRegistry healthChecks = new HealthCheckRegistry();

        final AdminClient adminClient = factory.build(lifecycle, healthChecks, Collections.emptyMap());
        assertThat(adminClient)
                .isNotNull();
    }

    @Test
    public void shouldBuildABasicKafkaAdminClientWithTopics() throws Exception {
        final File yml = new File(Resources.getResource("yaml/basic-admin.yaml").toURI());
        final KafkaAdminClientFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(KafkaAdminClientFactory.class);
        final LifecycleEnvironment lifecycle = new LifecycleEnvironment(metrics);
        final HealthCheckRegistry healthChecks = new HealthCheckRegistry();

        final AdminClient adminClient = factory.build(lifecycle, healthChecks, Collections.emptyMap());
        assertThat(adminClient)
                .isNotNull();
        assertThat(factory.getTopicCreationEnabled())
                .isTrue();
        assertThat(factory.getTopics().size())
                .isEqualTo(2);
    }



    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(BasicKafkaAdminClientFactory.class);
    }
}

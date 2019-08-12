package io.dropwizard.kafka;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.Test;

import javax.validation.Validator;
import java.io.File;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicAdminClientFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<BasicKafkaAdminClientFactory> configFactory =
            new YamlConfigurationFactory<>(BasicKafkaAdminClientFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildABasicAdminClient() throws Exception {
        final File yml = new File(Resources.getResource("yaml/basic-admin-no-topics.yaml").toURI());
        final KafkaAdminClientFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(KafkaAdminClientFactory.class);
        final LifecycleEnvironment lifecycle = new LifecycleEnvironment();
        final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();

        final AdminClient adminClient = factory.build(healthCheckRegistry, lifecycle, Collections.emptyMap());
        assertThat(adminClient)
                .isNotNull();
    }

    @Test
    public void shouldBuildABasicAdminClientWithTopics() throws Exception {
        final File yml = new File(Resources.getResource("yaml/basic-admin.yaml").toURI());
        final KafkaAdminClientFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(KafkaAdminClientFactory.class);
        final LifecycleEnvironment lifecycle = new LifecycleEnvironment();
        final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();

        final AdminClient adminClient = factory.build(healthCheckRegistry, lifecycle, Collections.emptyMap());
        assertThat(adminClient)
                .isNotNull();
        assertThat(factory.getTopicCreationEnabled())
                .isTrue();
        assertThat(factory.getTopics().size())
                .isEqualTo(2);
    }
}

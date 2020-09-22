package io.dropwizard.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.junit.Test;

import javax.validation.Validator;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnstableApiUsage")
public class KafkaTopicFactoryTest {

    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<KafkaTopicFactory> configFactory =
            new YamlConfigurationFactory<>(KafkaTopicFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildKafkaTopicsFactory() throws Exception {
        final File yml = new File(Resources.getResource("yaml/topic.yaml").toURI());
        final KafkaTopicFactory factory = configFactory.build(yml);
        assertThat(factory).isInstanceOf(KafkaTopicFactory.class);
    }

    @Test
    public void shouldBuildNewTopic() throws Exception {
        final File yml = new File(Resources.getResource("yaml/topic.yaml").toURI());
        final KafkaTopicFactory factory = configFactory.build(yml);

        NewTopic newTopic = factory.asNewTopic();
        assertThat(newTopic.name()).isEqualTo("foo");
        assertThat(newTopic.numPartitions()).isEqualTo(3);
        assertThat(newTopic.replicationFactor()).isEqualTo((short) 1);
        assertThat(newTopic.configs().get(TopicConfig.CLEANUP_POLICY_CONFIG)).isEqualTo(TopicConfig.CLEANUP_POLICY_COMPACT);
    }
}

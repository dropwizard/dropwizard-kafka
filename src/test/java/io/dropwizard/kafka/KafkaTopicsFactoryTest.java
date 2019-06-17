package io.dropwizard.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.junit.Test;

import javax.validation.Validator;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class KafkaTopicsFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<KafkaTopicsFactory> configFactory =
            new YamlConfigurationFactory<>(KafkaTopicsFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildKafkaTopicsFactory() throws Exception {
        final File yml = new File(Resources.getResource("yaml/topics.yaml").toURI());
        final KafkaTopicsFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(KafkaTopicsFactory.class);
    }

    @Test
    public void shouldBuildNewTopic() throws Exception {
        final File yml = new File(Resources.getResource("yaml/topics.yaml").toURI());
        final KafkaTopicsFactory factory = configFactory.build(yml);
        final Iterator<NewTopic> newTopicIterator = factory.getNewTopics().iterator();

        NewTopic topicWithoutConfig = newTopicIterator.next();
        assertThat(topicWithoutConfig.name()).isEqualTo("foo");
        assertThat(topicWithoutConfig.numPartitions()).isEqualTo(3);
        assertThat(topicWithoutConfig.replicationFactor()).isEqualTo((short) 1);
        assertThat(topicWithoutConfig.configs()).isNullOrEmpty();

        NewTopic topicWithConfig = newTopicIterator.next();
        assertThat(topicWithConfig.name()).isEqualTo("bar");
        assertThat(topicWithConfig.numPartitions()).isEqualTo(5);
        assertThat(topicWithConfig.replicationFactor()).isEqualTo((short) 2);
        Map<String, String> configs = new HashMap<>();
        configs.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT);
        assertThat(topicWithConfig.configs()).containsAllEntriesOf(configs);

        assertThat(newTopicIterator.hasNext()).isFalse();
    }

    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(KafkaTopicsFactory.class);
    }
}

package io.dropwizard.kafka.integration;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.kafka.KafkaConsumerFactory;
import io.dropwizard.kafka.KafkaProducerFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.kafka.test.core.BrokerAddress;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class DropwizardKafkaIT {
    private static final String PRODUCER_TOPIC = "producerTest";
    private static final String CONSUMER_TOPIC = "consumerTest";

    @ClassRule
    public static EmbeddedKafkaRule kafka = new EmbeddedKafkaRule(1, true, PRODUCER_TOPIC, CONSUMER_TOPIC);

    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<KafkaProducerFactory> producerConfigFactory =
            new YamlConfigurationFactory<>(KafkaProducerFactory.class, validator, objectMapper, "dw");
    private final YamlConfigurationFactory<KafkaConsumerFactory> consumerConfigFactory =
            new YamlConfigurationFactory<>(KafkaConsumerFactory.class, validator, objectMapper, "dw");

    @Test
    public void basicProducerShouldConnectToKafka() throws Exception {

        final File yml = new File(Resources.getResource("yml/integration/basic-producer.yml").toURI());
        final KafkaProducerFactory<String, String> factory = producerConfigFactory.build(yml);
        factory.setBootstrapServers(
                Arrays.stream(kafka.getEmbeddedKafka().getBrokerAddresses())
                        .map(BrokerAddress::toString)
                        .collect(Collectors.toSet())
        );
        final LifecycleEnvironment lifecycle = new LifecycleEnvironment();
        final HealthCheckRegistry healthChecks = new HealthCheckRegistry();

        try (final Producer<String, String> producer = factory.build(lifecycle, healthChecks, ImmutableList.of(PRODUCER_TOPIC),
                null)) {

            final ProducerRecord<String, String> record = new ProducerRecord<>(PRODUCER_TOPIC, "key", "value");
            final Future<RecordMetadata> metadataFuture = producer.send(record);

            // wait for message to be successfully produced
            final RecordMetadata metadata = metadataFuture.get();

            assertThat(metadata)
                    .isNotNull();
            assertThat(metadata.hasOffset())
                    .isTrue();
        }
    }

    @Test
    public void basicConsumerShouldConnectToKafka() throws Exception {
        final File yml = new File(Resources.getResource("yml/integration/basic-consumer.yml").toURI());
        final KafkaConsumerFactory factory = consumerConfigFactory.build(yml);
        factory.setBootstrapServers(
                Arrays.stream(kafka.getEmbeddedKafka().getBrokerAddresses())
                        .map(BrokerAddress::toString)
                        .collect(Collectors.toSet())
        );
        final LifecycleEnvironment lifecycle = new LifecycleEnvironment();
        final HealthCheckRegistry healthChecks = new HealthCheckRegistry();

        try (final Consumer consumer = factory.build(lifecycle, healthChecks, null, null)) {
            consumer.subscribe(ImmutableList.of(CONSUMER_TOPIC));
            final ConsumerRecords<String, String> foundRecords = consumer.poll(10L);

            assertThat(foundRecords)
                    .isEmpty();
        }
    }
}

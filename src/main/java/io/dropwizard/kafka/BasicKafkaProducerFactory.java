package io.dropwizard.kafka;

import brave.Tracing;
import brave.kafka.clients.KafkaTracing;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Joiner;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

@JsonTypeName("basic")
public class BasicKafkaProducerFactory<K, V> extends KafkaProducerFactory<K, V> {
    private static final Logger log = LoggerFactory.getLogger(BasicKafkaProducerFactory.class);

    @Override
    public Producer<K, V> build(final LifecycleEnvironment lifecycle,
                                final HealthCheckRegistry healthChecks,
                                final Collection<String> topics,
                                @Nullable final Tracing tracing,
                                final Map<String, Object> configOverrides) {
        final Map<String, Object> config = createBaseKafkaConfigurations();

        config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, String.join(",", bootstrapServers));

        if (!requireNonNull(configOverrides).isEmpty()) {
            config.putAll(configOverrides);
        }

        final Optional<KafkaTracing> kafkaTracing = Optional.ofNullable(getTracingFactory())
                .flatMap(tracingFactory -> tracingFactory.build(tracing));

        final Producer<K, V> rawProducer = buildProducer(config);

        final Producer<K, V> producer = kafkaTracing.map(kTracing -> kTracing.producer(rawProducer))
                .orElse(rawProducer);

        manageProducer(lifecycle, producer);

        registerProducerHealthCheck(healthChecks, producer, topics);

        return producer;
    }

    @Override
    public boolean isValidConfiguration() {
        final List<String> errors = new ArrayList<>();

        if (bootstrapServers != null && bootstrapServers.isEmpty()) {
            errors.add("bootstrapServers cannot be empty if basic type is configured");
        }

        if (!errors.isEmpty()) {
            final String errorMessage = Joiner.on(System.lineSeparator()).join(errors);
            log.error("Failed to construct a basic Kafka cluster connection, due to the following errors:{}{}", System.lineSeparator(),
                    errorMessage);
            return false;
        }

        return true;
    }
}

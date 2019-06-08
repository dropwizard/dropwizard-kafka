package io.dropwizard.kafka;

import brave.Tracing;
import brave.kafka.clients.KafkaTracing;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

@JsonTypeName("basic")
public class BasicKafkaConsumerFactory<K, V> extends KafkaConsumerFactory<K, V> {
    private static final Logger log = LoggerFactory.getLogger(BasicKafkaConsumerFactory.class);

    @Override
    public Consumer<K, V> build(final LifecycleEnvironment lifecycle,
                                final HealthCheckRegistry healthChecks,
                                @Nullable final Tracing tracing,
                                @Nullable final ConsumerRebalanceListener rebalanceListener,
                                final Map<String, Object> configOverrides) {
        final Map<String, Object> config = createBaseKafkaConfigurations();

        config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, String.join(",", bootstrapServers));

        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);

        if (!requireNonNull(configOverrides).isEmpty()) {
            config.putAll(configOverrides);
        }

        final Optional<KafkaTracing> kafkaTracing = Optional.ofNullable(getTracingFactory())
                .flatMap(tracingFactory -> tracingFactory.build(tracing));

        final Consumer<K, V> rawConsumer = buildConsumer(config);

        final Consumer<K, V> consumer = kafkaTracing.map(kTracing -> kTracing.consumer(rawConsumer))
                .orElse(rawConsumer);

        manageConsumer(lifecycle, consumer);

        registerHealthCheck(healthChecks, consumer);

        return consumer;
    }

    @Override
    public boolean isValidConfiguration() {
        final List<String> errors = new ArrayList<>();

        if (bootstrapServers != null && bootstrapServers.isEmpty()) {
            errors.add("bootstrapServers cannot be empty if basic type is configured");
        }

        if (!errors.isEmpty()) {
            final String errorMessage = String.join(System.lineSeparator(), errors);
            log.error("Failed to construct a BASIC Kafka cluster connection, due to the following errors:{}{}", System.lineSeparator(),
                    errorMessage);
            return false;
        }

        return true;
    }
}

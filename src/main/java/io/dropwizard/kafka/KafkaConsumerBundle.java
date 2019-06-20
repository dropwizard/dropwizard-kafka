package io.dropwizard.kafka;

import brave.Tracing;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public abstract class KafkaConsumerBundle<K, V, T extends Configuration> implements ConfiguredBundle<T> {
    private final Collection<String> topics;
    private final ConsumerRebalanceListener consumerRebalanceListener;
    private final Map<String, Object> configOverrides;

    @Nullable
    private Consumer<K, V> consumer;

    protected KafkaConsumerBundle(final Collection<String> topics,
                                  final ConsumerRebalanceListener consumerRebalanceListener) {
        this(topics, consumerRebalanceListener, Collections.emptyMap());
    }

    protected KafkaConsumerBundle(final Collection<String> topics,
                                  final ConsumerRebalanceListener consumerRebalanceListener,
                                  final Map<String, Object> configOverrides) {
        this.topics = Objects.requireNonNull(topics);
        this.consumerRebalanceListener = Objects.requireNonNull(consumerRebalanceListener);
        this.configOverrides = Objects.requireNonNull(configOverrides);
    }

    @Override
    public void initialize(final Bootstrap<?> bootstrap) {
        // do nothing
    }

    @Override
    public void run(final T configuration, final Environment environment) throws Exception {
        final KafkaConsumerFactory<K, V> kafkaConsumerFactory = requireNonNull(getKafkaConsumerFactory(configuration));

        final Tracing tracing = Tracing.current();

        this.consumer = kafkaConsumerFactory.build(environment.lifecycle(), environment.healthChecks(), tracing,
                consumerRebalanceListener, configOverrides);
    }

    public abstract KafkaConsumerFactory<K, V> getKafkaConsumerFactory(T configuration);

    public Consumer<K, V> getConsumer() {
        return requireNonNull(consumer);
    }
}

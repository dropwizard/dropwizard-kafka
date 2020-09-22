package io.dropwizard.kafka;

import brave.Tracing;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.kafka.clients.producer.Producer;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("unused")
public abstract class KafkaProducerBundle<K, V, T extends Configuration> implements ConfiguredBundle<T> {

    private final Collection<String> topics;
    private final Map<String, Object> configOverrides;

    @Nullable
    private Producer<K, V> producer;

    public KafkaProducerBundle(final Collection<String> topics) {
        this(topics, Collections.emptyMap());
    }

    public KafkaProducerBundle(final Collection<String> topics,
                               final Map<String, Object> configOverrides) {
        this.topics = requireNonNull(topics);
        this.configOverrides = requireNonNull(configOverrides);
    }

    @Override
    public void initialize(final Bootstrap<?> bootstrap) {
        // do nothing
    }

    @Override
    public void run(final T configuration, final Environment environment) throws Exception {
        final KafkaProducerFactory<K, V> kafkaProducerFactory = requireNonNull(getKafkaProducerFactory(configuration));

        final Tracing tracing = Tracing.current();

        this.producer = kafkaProducerFactory.build(environment.lifecycle(), environment.healthChecks(), topics, tracing, configOverrides);
    }

    public abstract KafkaProducerFactory<K, V> getKafkaProducerFactory(T configuration);

    public Producer<K, V> getProducer() {
        return requireNonNull(producer);
    }
}

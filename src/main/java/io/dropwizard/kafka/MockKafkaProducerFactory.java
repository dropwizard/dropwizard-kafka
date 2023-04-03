package io.dropwizard.kafka;

import brave.Tracing;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Producer;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Map;

@JsonTypeName("mock")
public class MockKafkaProducerFactory<K, V> extends KafkaProducerFactory<K, V> {
    @Override
    public Producer<K, V> build(LifecycleEnvironment lifecycle, HealthCheckRegistry healthChecks, Collection<String> topics, @Nullable Tracing tracing, Map<String, Object> configOverrides) {
        return new MockProducer<>();
    }

    @Override
    public boolean isValidConfiguration() {
        return true;
    }
}

package io.dropwizard.kafka.health;

import com.codahale.metrics.health.HealthCheck;
import org.apache.kafka.clients.producer.Producer;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class KafkaProducerHealthCheck extends HealthCheck {

    private final Producer producer;
    private final Collection<String> topics;

    public KafkaProducerHealthCheck(final Producer producer, final Collection<String> topics) {
        this.producer = requireNonNull(producer);
        this.topics = requireNonNull(topics);
    }

    @Override
    protected Result check() {
        try {
            topics.forEach(producer::partitionsFor);
            return Result.healthy();

        } catch (final Exception e) {
            return Result.unhealthy(e);
        }
    }
}

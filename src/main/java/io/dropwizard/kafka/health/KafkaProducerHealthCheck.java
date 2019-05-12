package io.dropwizard.kafka.health;

import com.codahale.metrics.health.HealthCheck;
import org.apache.kafka.clients.producer.Producer;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nonnull;

public class KafkaProducerHealthCheck extends HealthCheck {

    @Nonnull
    private final Producer producer;
    @Nonnull
    private final Collection<String> topics;

    public KafkaProducerHealthCheck(@Nonnull final Producer producer,
                                    @Nonnull final Collection<String> topics) {
        this.producer = Objects.requireNonNull(producer);
        this.topics = Objects.requireNonNull(topics);
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

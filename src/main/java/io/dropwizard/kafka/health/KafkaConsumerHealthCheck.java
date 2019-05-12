package io.dropwizard.kafka.health;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.kafka.CheckableConsumer;

import java.util.Objects;

import javax.annotation.Nonnull;

public class KafkaConsumerHealthCheck extends HealthCheck {

    @Nonnull
    private final CheckableConsumer consumer;

    public KafkaConsumerHealthCheck(@Nonnull final CheckableConsumer consumer) {
        this.consumer = Objects.requireNonNull(consumer);
    }

    @Override
    protected Result check() {
        if (consumer.isActive()) {
            return Result.healthy();
        } else {
            return Result.unhealthy("Consumer not subscribed to any topics");
        }
    }
}

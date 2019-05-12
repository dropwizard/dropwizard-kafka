package io.dropwizard.kafka.managed;

import io.dropwizard.lifecycle.Managed;
import org.apache.kafka.clients.consumer.Consumer;

import java.util.Objects;

import javax.annotation.Nonnull;

public class KafkaConsumerManager implements Managed {

    @Nonnull
    private final Consumer consumer;

    public KafkaConsumerManager(@Nonnull final Consumer consumer) {
        this.consumer = Objects.requireNonNull(consumer);
    }

    @Override
    public void start() {
        // do nothing, to prevent concurrent modification exceptions
    }

    @Override
    public void stop() {
        consumer.wakeup();
    }
}

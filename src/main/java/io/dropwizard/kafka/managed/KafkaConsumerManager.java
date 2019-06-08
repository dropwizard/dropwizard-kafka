package io.dropwizard.kafka.managed;

import io.dropwizard.lifecycle.Managed;
import org.apache.kafka.clients.consumer.Consumer;

import static java.util.Objects.requireNonNull;

public class KafkaConsumerManager implements Managed {

    private final Consumer consumer;

    public KafkaConsumerManager(final Consumer consumer) {
        this.consumer = requireNonNull(consumer);
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

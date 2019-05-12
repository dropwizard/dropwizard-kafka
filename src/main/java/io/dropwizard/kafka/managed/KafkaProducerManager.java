package io.dropwizard.kafka.managed;

import io.dropwizard.lifecycle.Managed;
import org.apache.kafka.clients.producer.Producer;

import java.util.Objects;

import javax.annotation.Nonnull;

public class KafkaProducerManager implements Managed {

    @Nonnull
    private final Producer producer;

    public KafkaProducerManager(@Nonnull final Producer producer) {
        this.producer = Objects.requireNonNull(producer);
    }

    @Override
    public void start() throws Exception {
        // do nothing
    }

    @Override
    public void stop() throws Exception {
        producer.close();
    }
}

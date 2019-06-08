package io.dropwizard.kafka.managed;

import io.dropwizard.lifecycle.Managed;
import org.apache.kafka.clients.producer.Producer;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class KafkaProducerManager implements Managed {

    private final Producer producer;

    public KafkaProducerManager(final Producer producer) {
        this.producer = requireNonNull(producer);
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

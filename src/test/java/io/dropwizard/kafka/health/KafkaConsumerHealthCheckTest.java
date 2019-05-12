package io.dropwizard.kafka.health;

import io.dropwizard.kafka.CheckableConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class KafkaConsumerHealthCheckTest {
    @Mock
    private Consumer<String, String> consumerMock;
    private CheckableConsumer<String, String> consumer;
    private KafkaConsumerHealthCheck healthCheck;

    @Before
    public void setUp() {
        this.consumer = new CheckableConsumer<>(consumerMock);
        this.healthCheck = new KafkaConsumerHealthCheck(consumer);
    }

    @Test
    public void shouldReturnHealthyWhenConsumerSubscribed() {
        consumer.subscribe(Collections.singletonList("topic"));

        assertThat(healthCheck.check().isHealthy())
                .isTrue();
    }

    @Test
    public void shouldReturnHealthyWhenConsumerUnsubscribed() {
        consumer.subscribe(Collections.singletonList("topic"));
        consumer.unsubscribe();

        assertThat(healthCheck.check().isHealthy())
                .isFalse();
    }

    @Test
    public void shouldReturnUnhealthyWhenConsumerNeverSubscribed() {
        assertThat(healthCheck.check().isHealthy())
                .isFalse();
    }
}

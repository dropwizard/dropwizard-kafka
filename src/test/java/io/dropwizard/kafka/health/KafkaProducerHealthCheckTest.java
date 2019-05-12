package io.dropwizard.kafka.health;

import com.google.common.collect.ImmutableList;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.errors.InterruptException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KafkaProducerHealthCheckTest {

    @Mock
    private KafkaProducer producerMock;
    private final List<String> topics = ImmutableList.of("testTopic1", "testTopic2");

    private KafkaProducerHealthCheck healthCheck;

    @Before
    public void setUp() {
        this.healthCheck = new KafkaProducerHealthCheck(producerMock, topics);
    }

    @Test
    public void shouldReturnHealthyWhenClusterReportsTopicMetadata() {
        topics.forEach(topic -> when(producerMock.partitionsFor(topic)).thenReturn(Collections.emptyList()));

        assertThat(healthCheck.check().isHealthy())
                .isTrue();
    }

    @Test
    public void shouldReturnUnhealthyWhenClusterFailsToReportTopicMetadata() {
        topics.forEach(topic -> when(producerMock.partitionsFor(topic)).thenThrow(new InterruptException("timed out waiting")));

        assertThat(healthCheck.check().isHealthy())
                .isFalse();
    }
}

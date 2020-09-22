package io.dropwizard.kafka.health;

import com.google.common.collect.ImmutableList;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.errors.InterruptException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class KafkaProducerHealthCheckTest {

    private final KafkaProducer producerMock = mock(KafkaProducer.class);
    private final List<String> topics = ImmutableList.of("testTopic1", "testTopic2");

    private final KafkaProducerHealthCheck healthCheck = new KafkaProducerHealthCheck(producerMock, topics);

    @Before
    public void setUp() {
        reset(producerMock);
    }

    @Test
    public void shouldReturnHealthyWhenClusterReportsTopicMetadata() {
        topics.forEach(topic -> when(producerMock.partitionsFor(topic)).thenReturn(Collections.emptyList()));

        assertThat(healthCheck.check().isHealthy(), is(true));
    }

    @Test
    public void shouldReturnUnhealthyWhenClusterFailsToReportTopicMetadata() {
        topics.forEach(topic -> when(producerMock.partitionsFor(topic)).thenThrow(new InterruptException("timed out waiting")));

        assertThat(healthCheck.check().isHealthy(), is(false));
    }
}

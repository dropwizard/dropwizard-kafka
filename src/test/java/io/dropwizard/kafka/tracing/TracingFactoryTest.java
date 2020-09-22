package io.dropwizard.kafka.tracing;

import brave.Tracing;
import brave.kafka.clients.KafkaTracing;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class TracingFactoryTest {

    private TracingFactory factory;

    @Before
    public void setUp() {
        this.factory = new TracingFactory();
    }

    @Test
    public void testFactoryBuildReturnsEmptyWhenTracingIsNull() {
        assertEquals(Optional.empty(), factory.build(null));
    }

    @Test
    public void testFactoryBuildReturnsEmptyWhenTracingIsDisabled() {
        factory.setEnabled(false);
        assertEquals(Optional.empty(), factory.build(mock(Tracing.class)));
    }

    @Test
    public void testFactoryBuild() {
        factory.setRemoteServiceName("remote");
        Optional<KafkaTracing> kafkaTracing = factory.build(Tracing.newBuilder().build());
        assertTrue(kafkaTracing.isPresent());
    }
}

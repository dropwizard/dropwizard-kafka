package io.dropwizard.kafka.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.DoubleSerializer;
import org.junit.Test;

import javax.validation.Validator;
import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnstableApiUsage")
public class DoubleSerializerFactoryTest {

    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<SerializerFactory> configFactory =
            new YamlConfigurationFactory<>(SerializerFactory.class, validator, objectMapper, "dw");

    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(DoubleSerializerFactory.class);
    }

    @Test
    public void shouldBuildADoubleSerializerSetOfConfigs() throws Exception {
        final File yml = new File(Resources.getResource("yaml/serializer/double.yaml").toURI());
        final SerializerFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(DoubleSerializerFactory.class);
        final Map<String, Object> config = factory.build(false);
        assertThat(config.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG))
                .isEqualTo(DoubleSerializer.class);
    }
}

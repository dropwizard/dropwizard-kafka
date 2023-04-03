package io.dropwizard.kafka.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ShortDeserializer;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import jakarta.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class ShortDeserializerFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<DeserializerFactory> configFactory =
            new YamlConfigurationFactory<>(DeserializerFactory.class, validator, objectMapper, "dw");

    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(ShortDeserializerFactory.class);
    }

    @Test
    public void shouldBuildAnShortDeserializerSetOfConfigs() throws Exception {
        final File yml = new File(Resources.getResource("yaml/deserializer/short.yaml").toURI());
        final DeserializerFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(ShortDeserializerFactory.class);
        final Map<String, Object> config = factory.build(true);
        assertThat(config.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG))
                .isEqualTo(ShortDeserializer.class);
    }
}

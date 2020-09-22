package io.dropwizard.kafka.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.FloatDeserializer;
import org.junit.Test;

import javax.validation.Validator;
import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnstableApiUsage")
public class FloatDeserializerFactoryTest {

    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<DeserializerFactory> configFactory =
            new YamlConfigurationFactory<>(DeserializerFactory.class, validator, objectMapper, "dw");

    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(FloatDeserializerFactory.class);
    }

    @Test
    public void shouldBuildAFloatDeserializerSetOfConfigs() throws Exception {
        final File yml = new File(Resources.getResource("yaml/deserializer/float.yaml").toURI());
        final DeserializerFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(FloatDeserializerFactory.class);
        final Map<String, Object> config = factory.build(false);
        assertThat(config.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG))
                .isEqualTo(FloatDeserializer.class);
    }
}

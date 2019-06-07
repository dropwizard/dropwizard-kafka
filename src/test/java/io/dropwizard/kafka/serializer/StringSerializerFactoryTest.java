package io.dropwizard.kafka.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class StringSerializerFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<SerializerFactory> configFactory =
            new YamlConfigurationFactory<>(SerializerFactory.class, validator, objectMapper, "dw");

    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(StringSerializerFactory.class);
    }

    @Test
    public void shouldBuildAStringSerializerSetOfConfigs() throws Exception {
        final File yml = new File(Resources.getResource("yaml/serializer/string.yaml").toURI());
        final SerializerFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(StringSerializerFactory.class);
        final Map<String, Object> config = factory.build(true);
        assertThat(config.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG))
                .isEqualTo(StringSerializer.class);
        assertThat(config.get("key.serializer.encoding")).isEqualTo("UTF8");
    }
}

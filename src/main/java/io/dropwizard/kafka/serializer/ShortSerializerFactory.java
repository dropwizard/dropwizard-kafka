package io.dropwizard.kafka.serializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ShortSerializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@JsonTypeName("short")
public class ShortSerializerFactory extends SerializerFactory {
    @Override
    public Map<String, Object> build(final boolean isKey) {
        final String propertyName = isKey ? ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG : ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

        final Map<String, Object> config = new HashMap<>();
        config.put(propertyName, ShortSerializer.class.getName());
        return Collections.unmodifiableMap(config);
    }
}

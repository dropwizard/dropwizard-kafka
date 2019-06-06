package io.dropwizard.kafka.deserializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.FloatDeserializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@JsonTypeName("float")
public class FloatDeserializerFactory extends DeserializerFactory {
    @Override
    public Map<String, Object> build(final boolean isKey) {
        final String propertyName = isKey ?
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG : ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

        final Map<String, Object> config = new HashMap<>();
        config.put(propertyName, FloatDeserializer.class.getName());
        return Collections.unmodifiableMap(config);
    }
}

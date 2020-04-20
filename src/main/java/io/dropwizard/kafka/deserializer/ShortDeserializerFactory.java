package io.dropwizard.kafka.deserializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.ShortDeserializer;

@JsonTypeName("short")
public class ShortDeserializerFactory extends DeserializerFactory {
    @Override
    public Class<? extends Deserializer<?>> getDeserializerClass() {
        return ShortDeserializer.class;
    }
}

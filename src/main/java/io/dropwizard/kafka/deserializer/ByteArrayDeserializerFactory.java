package io.dropwizard.kafka.deserializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.Deserializer;

@JsonTypeName("byte-array")
public class ByteArrayDeserializerFactory extends DeserializerFactory {
    @Override
    public Class<? extends Deserializer> getDeserializerClass() {
        return ByteArrayDeserializer.class;
    }
}

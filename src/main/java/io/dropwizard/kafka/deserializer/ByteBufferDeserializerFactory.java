package io.dropwizard.kafka.deserializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.common.serialization.ByteBufferDeserializer;
import org.apache.kafka.common.serialization.Deserializer;

@JsonTypeName("byte-buffer")
public class ByteBufferDeserializerFactory extends DeserializerFactory {
    @Override
    public Class<? extends Deserializer<?>> getDeserializerClass() {
        return ByteBufferDeserializer.class;
    }
}

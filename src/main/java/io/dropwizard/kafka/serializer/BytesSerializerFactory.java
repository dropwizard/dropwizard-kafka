package io.dropwizard.kafka.serializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.common.serialization.BytesSerializer;
import org.apache.kafka.common.serialization.Serializer;

@JsonTypeName("bytes")
public class BytesSerializerFactory extends SerializerFactory {
    @Override
    public Class<? extends Serializer<?>> getSerializerClass() {
        return BytesSerializer.class;
    }
}

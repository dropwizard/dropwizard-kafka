package io.dropwizard.kafka.serializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;

@JsonTypeName("byte-array")
public class ByteArraySerializerFactory extends SerializerFactory {

    @Override
    public Class<? extends Serializer<?>> getSerializerClass() {
        return ByteArraySerializer.class;
    }
}

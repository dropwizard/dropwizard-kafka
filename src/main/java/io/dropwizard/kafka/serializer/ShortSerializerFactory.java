package io.dropwizard.kafka.serializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.ShortSerializer;

@JsonTypeName("short")
public class ShortSerializerFactory extends SerializerFactory {
    @Override
    public Class<? extends Serializer> getSerializerClass() {
        return ShortSerializer.class;
    }
}

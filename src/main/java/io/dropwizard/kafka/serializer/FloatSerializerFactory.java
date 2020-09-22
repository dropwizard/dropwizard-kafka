package io.dropwizard.kafka.serializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.common.serialization.FloatSerializer;
import org.apache.kafka.common.serialization.Serializer;

@JsonTypeName("float")
public class FloatSerializerFactory extends SerializerFactory {

    @Override
    public Class<? extends Serializer<?>> getSerializerClass() {
        return FloatSerializer.class;
    }
}

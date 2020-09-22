package io.dropwizard.kafka.serializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serializer;

@JsonTypeName("long")
public class LongSerializerFactory extends SerializerFactory {

    @Override
    public Class<? extends Serializer<?>> getSerializerClass() {
        return LongSerializer.class;
    }
}

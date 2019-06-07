package io.dropwizard.kafka.serializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serializer;

@JsonTypeName("integer")
public class IntegerSerializerFactory extends SerializerFactory {
    @Override
    public Class<? extends Serializer> getSerializerClass() {
        return IntegerSerializer.class;
    }
}

package io.dropwizard.kafka.serializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.common.serialization.DoubleSerializer;
import org.apache.kafka.common.serialization.Serializer;

@JsonTypeName("double")
public class DoubleSerializerFactory extends SerializerFactory {
    @Override
    public Class<? extends Serializer> getSerializerClass() {
        return DoubleSerializer.class;
    }
}

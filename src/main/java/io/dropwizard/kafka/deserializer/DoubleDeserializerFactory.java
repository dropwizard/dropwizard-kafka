package io.dropwizard.kafka.deserializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.DoubleDeserializer;

@JsonTypeName("double")
public class DoubleDeserializerFactory extends DeserializerFactory {
    @Override
    public Class<? extends Deserializer<?>> getDeserializerClass() {
        return DoubleDeserializer.class;
    }
}

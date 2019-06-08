package io.dropwizard.kafka.deserializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.FloatDeserializer;

@JsonTypeName("float")
public class FloatDeserializerFactory extends DeserializerFactory {
    @Override
    public Class<? extends Deserializer> getDeserializerClass() {
        return FloatDeserializer.class;
    }
}

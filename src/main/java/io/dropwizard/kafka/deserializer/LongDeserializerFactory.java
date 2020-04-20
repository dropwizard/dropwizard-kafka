package io.dropwizard.kafka.deserializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.LongDeserializer;

@JsonTypeName("long")
public class LongDeserializerFactory extends DeserializerFactory {
    @Override
    public Class<? extends Deserializer<?>> getDeserializerClass() {
        return LongDeserializer.class;
    }
}

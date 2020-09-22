package io.dropwizard.kafka.deserializer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.kafka.common.serialization.BytesDeserializer;
import org.apache.kafka.common.serialization.Deserializer;

@JsonTypeName("bytes")
public class BytesDeserializerFactory extends DeserializerFactory {

    @Override
    public Class<? extends Deserializer<?>> getDeserializerClass() {
        return BytesDeserializer.class;
    }
}

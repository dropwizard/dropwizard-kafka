package io.dropwizard.kafka.serializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public abstract class SerializerFactory implements Discoverable {
    public abstract Map<String, Object> build(final boolean isKey);
}

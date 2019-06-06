package io.dropwizard.kafka.deserializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public abstract class DeserializerFactory implements Discoverable {
    public abstract Map<String, Object> build(final boolean isKey);
}

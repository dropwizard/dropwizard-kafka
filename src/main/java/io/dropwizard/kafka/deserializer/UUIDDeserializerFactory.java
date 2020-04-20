package io.dropwizard.kafka.deserializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.validation.ValidationMethod;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.UUIDDeserializer;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

@JsonTypeName("uuid")
public class UUIDDeserializerFactory extends DeserializerFactory {
    @NotNull
    @JsonProperty
    private String encoding = "UTF8";

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    @JsonIgnore
    @ValidationMethod(message = "Invalid charset used for UUIDDeserializerFactory")
    public boolean isEncodingValid() {
        return Charset.isSupported(encoding);
    }

    @Override
    public Class<? extends Deserializer<?>> getDeserializerClass() {
        return UUIDDeserializer.class;
    }

    @Override
    public Map<String, Object> build(final boolean isKey) {
        final String propertyName;
        if (isKey) {
            propertyName = ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
        }
        else {
            propertyName = ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
        }
        
        final String encodingPropertyName;
        if (isKey) {
            encodingPropertyName = "key.deserializer.encoding";
        }
        else {
            encodingPropertyName = "value.deserializer.encoding";
        }

        final Map<String, Object> config = new HashMap<>();
        config.put(propertyName, getDeserializerClass());
        config.put(encodingPropertyName, encoding);
        return Collections.unmodifiableMap(config);
    }
}

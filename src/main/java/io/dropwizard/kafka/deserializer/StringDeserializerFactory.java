package io.dropwizard.kafka.deserializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.validation.ValidationMethod;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.NotNull;

@JsonTypeName("string")
public class StringDeserializerFactory extends DeserializerFactory {
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
    @ValidationMethod(message = "Invalid charset used for StringDeserializerFactory")
    public boolean isEncodingValid() {
        return Charset.isSupported(encoding);
    }

    @Override
    public Class<? extends Deserializer<?>> getDeserializerClass() {
        return StringDeserializer.class;
    }

    @Override
    public Map<String, Object> build(final boolean isKey) {
        final String deserializerPropertyName;
        final String encodingPropertyName;
        if (isKey) {
            deserializerPropertyName = ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
            encodingPropertyName = "key.deserializer.encoding";
        } else {
            deserializerPropertyName = ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
            encodingPropertyName = "value.deserializer.encoding";
        }

        final Map<String, Object> config = new HashMap<>();
        config.put(deserializerPropertyName, getDeserializerClass());
        config.put(encodingPropertyName, encoding);
        return Collections.unmodifiableMap(config);
    }
}

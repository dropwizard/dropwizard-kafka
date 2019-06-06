package io.dropwizard.kafka.deserializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.validation.ValidationMethod;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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
    @ValidationMethod(message = "Invalid charset used for StringDeserializerFactory")
    public boolean isEncodingValid() {
        return Charset.isSupported(encoding);
    }
    @Override
    public Map<String, Object> build(final boolean isKey) {
        final String propertyName = isKey ?
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG : ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
        final String encodingPropertyName = isKey ? "key.deserializer.encoding" : "value.deserializer.encoding";

        final Map<String, Object> config = new HashMap<>();
        config.put(propertyName, UUIDDeserializer.class.getName());
        config.put(encodingPropertyName, encoding);
        return Collections.unmodifiableMap(config);
    }
}

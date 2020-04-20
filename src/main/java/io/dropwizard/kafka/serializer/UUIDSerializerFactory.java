package io.dropwizard.kafka.serializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.validation.ValidationMethod;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.UUIDSerializer;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

@JsonTypeName("uuid")
public class UUIDSerializerFactory extends SerializerFactory {
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
    @ValidationMethod(message = "Invalid charset used for UUIDSerializerFactory")
    public boolean isEncodingValid() {
        return Charset.isSupported(encoding);
    }

    @Override
    public Class<? extends Serializer<?>> getSerializerClass() {
        return UUIDSerializer.class;
    }

    @Override
    public Map<String, Object> build(final boolean isKey) {
        final String propertyName;
        if (isKey) {
            propertyName = ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
        }
        else {
            propertyName = ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
        }
        final String encodingPropertyName;
        if (isKey) {
            encodingPropertyName = "key.serializer.encoding";
        }
        else {
            encodingPropertyName = "value.serializer.encoding";
        }

        final Map<String, Object> config = new HashMap<>();
        config.put(propertyName, getSerializerClass());
        config.put(encodingPropertyName, encoding);
        return Collections.unmodifiableMap(config);
    }
}

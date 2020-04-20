package io.dropwizard.kafka.serializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.validation.ValidationMethod;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

@JsonTypeName("string")
public class StringSerializerFactory extends SerializerFactory {
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
    @ValidationMethod(message = "Invalid charset used for StringSerializerFactory")
    public boolean isEncodingValid() {
        return Charset.isSupported(encoding);
    }

    @Override
    public Class<? extends Serializer<?>> getSerializerClass() {
        return StringSerializer.class;
    }

    @Override
    public Map<String, Object> build(final boolean isKey) {
        final String serializerPropertyName;
        final String encodingPropertyName;
        if (isKey) {
            serializerPropertyName = ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
            encodingPropertyName = "key.serializer.encoding";
        } else {
            serializerPropertyName = ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
            encodingPropertyName = "value.serializer.encoding";
        }

        final Map<String, Object> config = new HashMap<>();
        config.put(serializerPropertyName, getSerializerClass());
        config.put(encodingPropertyName, encoding);
        return Collections.unmodifiableMap(config);
    }
}

package io.dropwizard.kafka.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

import java.util.Map;

import javax.validation.constraints.NotEmpty;

@JsonTypeName("sasl_plaintext")
public class SaslPlaintextSecurityFactory extends SecurityFactory {

    @NotEmpty
    @JsonProperty
    private String saslMechanism = "PLAIN";

    @JsonProperty
    private String saslJaas;

    public String getSaslMechanism() {
        return saslMechanism;
    }

    public void setSaslMechanism(final String saslMechanism) {
        this.saslMechanism = saslMechanism;
    }

    public String getSaslJaas() {
        return saslJaas;
    }

    public void setSaslJaas(final String saslJaas) {
        this.saslJaas = saslJaas;
    }

    @Override
    public Map<String, Object> build() {
        final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
                .put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol.toUpperCase())
                .put(SaslConfigs.SASL_MECHANISM, saslMechanism)
                .put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, sslEndpointIdentificationAlgorithm);

        if (saslJaas != null) {
            builder.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaas);
        }

        return builder.build();
    }
}

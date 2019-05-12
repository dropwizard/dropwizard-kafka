package io.dropwizard.kafka.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Map;
import java.util.Optional;

@JsonTypeName("sasl_ssl")
public class SaslSslSecurityFactory extends SecurityFactory {

    @NotEmpty
    @JsonProperty
    private String sslProtocol = "TLSv1.2";

    @NotEmpty
    @JsonProperty
    private String saslMechanism = "PLAIN";

    @JsonProperty
    private Optional<String> saslJaas = Optional.empty();

    public String getSslProtocol() {
        return sslProtocol;
    }

    public void setSslProtocol(final String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    public String getSaslMechanism() {
        return saslMechanism;
    }

    public void setSaslMechanism(final String saslMechanism) {
        this.saslMechanism = saslMechanism;
    }

    @Override
    public Map<String, Object> build() {
        final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
                .put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol.toUpperCase())
                .put(SslConfigs.SSL_PROTOCOL_CONFIG, sslProtocol)
                .put(SaslConfigs.SASL_MECHANISM, saslMechanism);

        saslJaas.ifPresent(jaas -> builder.put(SaslConfigs.SASL_JAAS_CONFIG, jaas));

        return builder.build();
    }
}

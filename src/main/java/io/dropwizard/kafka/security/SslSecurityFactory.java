package io.dropwizard.kafka.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Map;

@JsonTypeName("ssl")
public class SslSecurityFactory extends SecurityFactory {

    @NotEmpty
    @JsonProperty
    private String sslProtocol = "TLSv1.2";

    public String getSslProtocol() {
        return sslProtocol;
    }

    public void setSslProtocol(final String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    @Override
    public Map<String, Object> build() {
        return ImmutableMap.of(
                CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol.toUpperCase(),
                SslConfigs.SSL_PROTOCOL_CONFIG, sslProtocol
        );
    }
}

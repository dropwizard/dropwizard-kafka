package io.dropwizard.kafka.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static org.apache.kafka.common.config.SslConfigs.DEFAULT_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "securityProtocol", visible = true)
public abstract class SecurityFactory {

    @JsonProperty
    private boolean enabled = true;

    @NotEmpty
    @JsonProperty
    protected String securityProtocol;

    @NotNull
    @JsonProperty
    protected String sslEndpointIdentificationAlgorithm = DEFAULT_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getSecurityProtocol() {
        return securityProtocol;
    }

    public void setSecurityProtocol(final String securityProtocol) {
        this.securityProtocol = securityProtocol;
    }

    public String getSslEndpointIdentificationAlgorithm() {
        return sslEndpointIdentificationAlgorithm;
    }

    public void setSslEndpointIdentificationAlgorithm(final String sslEndpointIdentificationAlgorithm) {
        this.sslEndpointIdentificationAlgorithm = sslEndpointIdentificationAlgorithm;
    }

    public abstract Map<String, Object> build();
}

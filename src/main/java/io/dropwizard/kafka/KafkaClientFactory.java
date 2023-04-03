package io.dropwizard.kafka;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.kafka.security.SecurityFactory;
import io.dropwizard.kafka.tracing.TracingFactory;
import io.dropwizard.validation.ValidationMethod;

import java.util.Optional;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public abstract class KafkaClientFactory {
    @NotEmpty
    @JsonProperty
    protected String name;

    @Valid
    @JsonProperty
    protected Optional<SecurityFactory> security = Optional.empty();

    @JsonProperty
    protected boolean metricsEnabled = true;

    @JsonProperty
    protected boolean includeTaggedMetrics = false;

    @JsonProperty
    protected Set<String> bootstrapServers;

    @JsonProperty
    protected Optional<String> clientDNSLookup = Optional.empty();

    @JsonProperty
    protected Optional<String> clientId = Optional.empty();

    @Valid
    @JsonProperty
    private TracingFactory tracingFactory;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Optional<SecurityFactory> getSecurity() {
        return security;
    }

    public void setSecurity(final Optional<SecurityFactory> security) {
        this.security = security;
    }

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public void setMetricsEnabled(final boolean metricsEnabled) {
        this.metricsEnabled = metricsEnabled;
    }

    public boolean isIncludeTaggedMetrics() {
        return includeTaggedMetrics;
    }

    public void setIncludeTaggedMetrics(final boolean includeTaggedMetrics) {
        this.includeTaggedMetrics = includeTaggedMetrics;
    }

    public Set<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public Optional<String> getClientDNSLookup() {
        return clientDNSLookup;
    }

    public void setClientDNSLookup(Optional<String> clientDNSLookup) {
        this.clientDNSLookup = clientDNSLookup;
    }

    public Optional<String> getClientId() {
        return clientId;
    }

    public void setClientId(Optional<String> clientId) {
        this.clientId = clientId;
    }

    public void setBootstrapServers(final Set<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public TracingFactory getTracingFactory() {
        return tracingFactory;
    }

    public void setTracingFactory(final TracingFactory tracingFactory) {
        this.tracingFactory = tracingFactory;
    }

    @ValidationMethod(message = "Some Kafka configurations were invalid")
    @JsonIgnore
    public abstract boolean isValidConfiguration();
}

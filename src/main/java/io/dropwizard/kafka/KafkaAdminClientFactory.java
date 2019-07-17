package io.dropwizard.kafka;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.kafka.health.KafkaAdminHealthCheck;
import io.dropwizard.kafka.managed.KafkaAdminClientManager;
import io.dropwizard.kafka.metrics.DropwizardMetricsReporter;
import io.dropwizard.kafka.security.SecurityFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.util.Duration;
import org.apache.kafka.clients.ClientDnsLookup;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.metrics.MetricsReporter;
import org.apache.kafka.common.metrics.Sensor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
public abstract class KafkaAdminClientFactory {
    @NotNull
    @JsonProperty
    protected String name;
    @NotNull
    @JsonProperty
    protected String clientId;
    @JsonProperty
    protected Set<String> bootstrapServers;
    @NotNull
    @JsonProperty
    protected ClientDnsLookup clientDnsLookup = ClientDnsLookup.DEFAULT;
    @NotNull
    @JsonProperty
    protected Duration reconnectBackoff = Duration.milliseconds(50);
    @NotNull
    @JsonProperty
    protected Duration reconnectBackoffMax = Duration.seconds(1);
    @Min(0)
    @NotNull
    @JsonProperty
    protected Integer retries = 5;
    @NotNull
    @JsonProperty
    protected Duration retryBackoff = Duration.milliseconds(100);
    @NotNull
    @JsonProperty
    protected Duration connectionMaxIdle = Duration.minutes(5);
    @NotNull
    @JsonProperty
    protected Duration requestTimeout = Duration.minutes(2);
    @NotNull
    @JsonProperty
    protected Duration metadataMaxAge = Duration.minutes(5);
    @Min(-1)
    @NotNull
    @JsonProperty
    protected Integer sendBufferBytes = 131072; // default taken from AdminClientConfig
    @Min(-1)
    @NotNull
    @JsonProperty
    protected Integer receiveBufferBytes = 65536; // default taken from AdminClientConfig
    @NotNull
    @JsonProperty
    protected List<Class<? extends MetricsReporter>> metricsReporters = Collections.singletonList(DropwizardMetricsReporter.class);
    @Min(1)
    @NotNull
    @JsonProperty
    protected Integer metricsSamples = 2; // default in AdminClientConfig
    @NotNull
    @JsonProperty
    protected Duration metricsSampleWindow = Duration.seconds(30);
    @NotNull
    @JsonProperty
    protected Sensor.RecordingLevel metricsRecordingLevel = Sensor.RecordingLevel.INFO;
    @Valid
    @JsonProperty
    protected SecurityFactory security;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public Set<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(final Set<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public ClientDnsLookup getClientDnsLookup() {
        return clientDnsLookup;
    }

    public void setClientDnsLookup(final ClientDnsLookup clientDnsLookup) {
        this.clientDnsLookup = clientDnsLookup;
    }

    public Duration getReconnectBackoff() {
        return reconnectBackoff;
    }

    public void setReconnectBackoff(final Duration reconnectBackoff) {
        this.reconnectBackoff = reconnectBackoff;
    }

    public Duration getReconnectBackoffMax() {
        return reconnectBackoffMax;
    }

    public void setReconnectBackoffMax(final Duration reconnectBackoffMax) {
        this.reconnectBackoffMax = reconnectBackoffMax;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(final Integer retries) {
        this.retries = retries;
    }

    public Duration getRetryBackoff() {
        return retryBackoff;
    }

    public void setRetryBackoff(final Duration retryBackoff) {
        this.retryBackoff = retryBackoff;
    }

    public Duration getConnectionMaxIdle() {
        return connectionMaxIdle;
    }

    public void setConnectionMaxIdle(final Duration connectionMaxIdle) {
        this.connectionMaxIdle = connectionMaxIdle;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(final Duration requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Duration getMetadataMaxAge() {
        return metadataMaxAge;
    }

    public void setMetadataMaxAge(final Duration metadataMaxAge) {
        this.metadataMaxAge = metadataMaxAge;
    }

    public Integer getSendBufferBytes() {
        return sendBufferBytes;
    }

    public void setSendBufferBytes(final Integer sendBufferBytes) {
        this.sendBufferBytes = sendBufferBytes;
    }

    public Integer getReceiveBufferBytes() {
        return receiveBufferBytes;
    }

    public void setReceiveBufferBytes(final Integer receiveBufferBytes) {
        this.receiveBufferBytes = receiveBufferBytes;
    }

    public List<Class<? extends MetricsReporter>> getMetricsReporters() {
        return metricsReporters;
    }

    public void setMetricsReporters(final List<Class<? extends MetricsReporter>> metricsReporters) {
        this.metricsReporters = metricsReporters;
    }

    public Integer getMetricsSamples() {
        return metricsSamples;
    }

    public void setMetricsSamples(final Integer metricsSamples) {
        this.metricsSamples = metricsSamples;
    }

    public Duration getMetricsSampleWindow() {
        return metricsSampleWindow;
    }

    public void setMetricsSampleWindow(final Duration metricsSampleWindow) {
        this.metricsSampleWindow = metricsSampleWindow;
    }

    public Sensor.RecordingLevel getMetricsRecordingLevel() {
        return metricsRecordingLevel;
    }

    public void setMetricsRecordingLevel(final Sensor.RecordingLevel metricsRecordingLevel) {
        this.metricsRecordingLevel = metricsRecordingLevel;
    }

    public SecurityFactory getSecurity() {
        return security;
    }

    public void setSecurity(final SecurityFactory security) {
        this.security = security;
    }

    protected AdminClient buildAdminClient(final Map<String, Object> config) {
        return AdminClient.create(config);
    }

    protected void manageAdminClient(final LifecycleEnvironment lifecycle, final AdminClient adminClient) {
        manageAdminClient(lifecycle, adminClient, null);
    }

    protected void manageAdminClient(final LifecycleEnvironment lifecycle, final AdminClient adminClient,
                                               Collection<NewTopic> topics) {
        lifecycle.manage(new KafkaAdminClientManager(adminClient, name, topics));
    }

    protected void registerHealthCheck(final HealthCheckRegistry healthChecks, final AdminClient adminClient) {
        healthChecks.register(name, new KafkaAdminHealthCheck(adminClient, name));
    }

    public abstract AdminClient build(HealthCheckRegistry healthChecks, LifecycleEnvironment lifecycle, Map<String, Object> config);

    public abstract AdminClient build(HealthCheckRegistry healthChecks, LifecycleEnvironment lifecycle, Map<String, Object> config, Collection<NewTopic> topics);
}

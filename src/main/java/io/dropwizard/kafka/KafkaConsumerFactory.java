package io.dropwizard.kafka;

import brave.Tracing;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import io.dropwizard.kafka.deserializer.DeserializerFactory;
import io.dropwizard.kafka.managed.KafkaConsumerManager;
import io.dropwizard.kafka.metrics.DropwizardMetricsReporter;
import io.dropwizard.kafka.security.SecurityFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.util.Duration;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public abstract class KafkaConsumerFactory<K, V> extends KafkaClientFactory implements Discoverable {
    @NotEmpty
    @JsonProperty
    protected String consumerGroupId;

    @Valid
    @NotNull
    @JsonProperty
    protected DeserializerFactory keyDeserializer;

    @Valid
    @NotNull
    @JsonProperty
    protected DeserializerFactory valueDeserializer;

    @JsonProperty
    protected boolean autoCommitEnabled = true;

    @JsonProperty
    protected Duration autoCommitInterval = Duration.seconds(5);

    @Min(-1)
    @JsonProperty
    protected int sendBufferBytes = -1;

    @Min(-1)
    @JsonProperty
    protected int receiveBufferBytes = -1;

    @Min(1)
    @JsonProperty
    protected int maxPollRecords = 500;

    @NotNull
    @JsonProperty
    protected Duration maxPollInterval = Duration.minutes(5);

    public String getConsumerGroupId() {
        return consumerGroupId;
    }

    public void setConsumerGroupId(final String consumerGroupId) {
        this.consumerGroupId = consumerGroupId;
    }

    public DeserializerFactory getKeyDeserializer() {
        return keyDeserializer;
    }

    public void setKeyDeserializer(final DeserializerFactory keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
    }

    public DeserializerFactory getValueDeserializer() {
        return valueDeserializer;
    }

    public void setValueDeserializer(final DeserializerFactory valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
    }

    public boolean isAutoCommitEnabled() {
        return autoCommitEnabled;
    }

    public void setAutoCommitEnabled(final boolean autoCommitEnabled) {
        this.autoCommitEnabled = autoCommitEnabled;
    }

    public Duration getAutoCommitInterval() {
        return autoCommitInterval;
    }

    public void setAutoCommitInterval(final Duration autoCommitInterval) {
        this.autoCommitInterval = autoCommitInterval;
    }

    public int getSendBufferBytes() {
        return sendBufferBytes;
    }

    public void setSendBufferBytes(final int sendBufferBytes) {
        this.sendBufferBytes = sendBufferBytes;
    }

    public int getReceiveBufferBytes() {
        return receiveBufferBytes;
    }

    public void setReceiveBufferBytes(final int receiveBufferBytes) {
        this.receiveBufferBytes = receiveBufferBytes;
    }

    public int getMaxPollRecords() {
        return maxPollRecords;
    }

    public void setMaxPollRecords(final int maxPollRecords) {
        this.maxPollRecords = maxPollRecords;
    }

    public Duration getMaxPollInterval() {
        return maxPollInterval;
    }

    public void setMaxPollInterval(final Duration maxPollInterval) {
        this.maxPollInterval = maxPollInterval;
    }

    protected Map<String, Object> createBaseKafkaConfigurations() {
        final Map<String, Object> config = new HashMap<>();

        config.putAll(keyDeserializer.build(true));
        config.putAll(valueDeserializer.build(false));

        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommitEnabled);
        if (autoCommitEnabled && autoCommitInterval != null) {
            config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, (int) autoCommitInterval.toMilliseconds());
        }

        clientDNSLookup.ifPresent(clientIdValue -> config.put(CommonClientConfigs.CLIENT_DNS_LOOKUP_CONFIG, clientIdValue));
        clientId.ifPresent(clientIdValue -> config.put(CommonClientConfigs.CLIENT_ID_CONFIG, clientIdValue));

        config.put(ConsumerConfig.SEND_BUFFER_CONFIG, sendBufferBytes);
        config.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, receiveBufferBytes);
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, (int) maxPollInterval.toMilliseconds());

        security.filter(SecurityFactory::isEnabled)
                .ifPresent(securityFactory -> config.putAll(securityFactory.build()));

        if (metricsEnabled) {
            config.put(DropwizardMetricsReporter.SHOULD_INCLUDE_TAGS_CONFIG, includeTaggedMetrics);
            config.put(CommonClientConfigs.METRIC_REPORTER_CLASSES_CONFIG, DropwizardMetricsReporter.class.getName());
            config.put(DropwizardMetricsReporter.METRICS_NAME_CONFIG, name);
        }

        return config;
    }

    protected Consumer<K, V> buildConsumer(final Map<String, Object> config) {
        return new KafkaConsumer<>(config);
    }

    protected void registerHealthCheck(final HealthCheckRegistry healthChecks, final Consumer<K, V> consumer) {
        // no consumer health checks, due to kafka client limitations. The admin client health check is the better option in general
    }

    protected void manageConsumer(final LifecycleEnvironment lifecycle, final Consumer<K, V> consumer) {
        lifecycle.manage(new KafkaConsumerManager(consumer));
    }

    public Consumer<K, V> build(final LifecycleEnvironment lifecycle,
                                final HealthCheckRegistry healthChecks,
                                @Nullable final Tracing tracing,
                                @Nullable final ConsumerRebalanceListener rebalanceListener) {
        return build(lifecycle, healthChecks, tracing, rebalanceListener, Collections.emptyMap());
    }

    public abstract Consumer<K, V> build(final LifecycleEnvironment lifecycle,
                                         final HealthCheckRegistry healthChecks,
                                         @Nullable Tracing tracing,
                                         @Nullable ConsumerRebalanceListener rebalanceListener,
                                         Map<String, Object> configOverrides);
}

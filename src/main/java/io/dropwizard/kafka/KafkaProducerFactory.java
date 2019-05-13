package io.dropwizard.kafka;

import brave.Tracing;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import io.dropwizard.kafka.metrics.DropwizardMetricsReporter;
import io.dropwizard.kafka.security.SecurityFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.util.Duration;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.record.CompressionType;
import org.apache.kafka.common.serialization.Serializer;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public abstract class KafkaProducerFactory<K, V> extends KafkaClientFactory implements Discoverable {
    @NotEmpty
    @JsonProperty
    protected String keySerializerClass;

    @NotEmpty
    @JsonProperty
    protected String valueSerializerClass;

    @JsonProperty
    protected Optional<String> acks = Optional.empty();

    @JsonProperty
    protected Optional<String> retries = Optional.empty();

    @JsonProperty
    protected Optional<Integer> maxInFlightRequestsPerConnection = Optional.empty();

    @JsonProperty
    protected Optional<Duration> maxPollBlockTime = Optional.empty();

    @NotEmpty
    @JsonProperty
    protected String compressionType = CompressionType.GZIP.name;

    @Min(-1)
    @JsonProperty
    protected int sendBufferBytes = -1;

    @Min(-1)
    @JsonProperty
    protected int receiveBufferBytes = -1;

    @Min(0L)
    @JsonProperty
    protected long bufferMemory = 32 * 1024 * 1024L;

    @Min(0)
    @JsonProperty
    protected int batchSize = 16384;

    public String getKeySerializerClass() {
        return keySerializerClass;
    }

    public void setKeySerializerClass(final String keySerializerClass) {
        this.keySerializerClass = keySerializerClass;
    }

    public Optional<String> getAcks() {
        return acks;
    }

    public void setAcks(final Optional<String> acks) {
        this.acks = acks;
    }

    public Optional<String> getRetries() {
        return retries;
    }

    public void setRetries(final Optional<String> retries) {
        this.retries = retries;
    }

    public Optional<Integer> getMaxInFlightRequestsPerConnection() {
        return maxInFlightRequestsPerConnection;
    }

    public void setMaxInFlightRequestsPerConnection(final Optional<Integer> maxInFlightRequestsPerConnection) {
        this.maxInFlightRequestsPerConnection = maxInFlightRequestsPerConnection;
    }

    public Optional<Duration> getMaxPollBlockTime() {
        return maxPollBlockTime;
    }

    public void setMaxPollBlockTime(final Optional<Duration> maxPollBlockTime) {
        this.maxPollBlockTime = maxPollBlockTime;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(final String compressionType) {
        this.compressionType = compressionType;
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

    public long getBufferMemory() {
        return bufferMemory;
    }

    public void setBufferMemory(final long bufferMemory) {
        this.bufferMemory = bufferMemory;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(final int batchSize) {
        this.batchSize = batchSize;
    }

    public String getValueSerializerClass() {
        return valueSerializerClass;
    }

    public void setValueSerializerClass(final String valueSerializerClass) {
        this.valueSerializerClass = valueSerializerClass;
    }

    protected Map<String, Object> createBaseKafkaConfigurations() {
        final Map<String, Object> config = new HashMap<>();

        DropwizardKafkaUtils.validateStringIsValidSubClass(keySerializerClass, Serializer.class);
        DropwizardKafkaUtils.validateStringIsValidSubClass(valueSerializerClass, Serializer.class);

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);

        security.filter(SecurityFactory::isEnabled)
                .ifPresent(securityFactory -> config.putAll(securityFactory.build()));
        acks.ifPresent(acksValue -> config.put(ProducerConfig.ACKS_CONFIG, acksValue));
        retries.ifPresent(retriesValue -> config.put(ProducerConfig.RETRIES_CONFIG, retriesValue));
        maxInFlightRequestsPerConnection.ifPresent(maxInFlightRequestsPerConnectionValue ->
                config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequestsPerConnectionValue));
        maxPollBlockTime.ifPresent(maxPollBlockTimeValue ->
                config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxPollBlockTimeValue.toMilliseconds()));

        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
        config.put(ProducerConfig.SEND_BUFFER_CONFIG, sendBufferBytes);
        config.put(ProducerConfig.RECEIVE_BUFFER_CONFIG, receiveBufferBytes);
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);

        if (metricsEnabled) {
            config.put(DropwizardMetricsReporter.SHOULD_INCLUDE_TAGS_CONFIG, Boolean.toString(includeTaggedMetrics));
            config.put(CommonClientConfigs.METRIC_REPORTER_CLASSES_CONFIG, DropwizardMetricsReporter.class.getName());
            config.put(DropwizardMetricsReporter.METRICS_NAME_CONFIG, name);
        }

        return config;
    }

    public Producer<K, V> build(final LifecycleEnvironment lifecycle,
                                final HealthCheckRegistry healthChecks,
                                final Collection<String> rawTopics,
                                @Nullable final Tracing tracing) {
        return build(lifecycle, healthChecks, rawTopics, tracing, Collections.emptyMap());
    }

    public abstract Producer<K, V> build(final LifecycleEnvironment lifecycle,
                                         final HealthCheckRegistry healthChecks,
                                         Collection<String> rawTopics,
                                         @Nullable Tracing tracing,
                                         Map<String, Object> configOverrides);
}

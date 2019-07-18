package io.dropwizard.kafka;

import brave.Tracing;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import io.dropwizard.kafka.health.KafkaProducerHealthCheck;
import io.dropwizard.kafka.managed.KafkaProducerManager;
import io.dropwizard.kafka.metrics.DropwizardMetricsReporter;
import io.dropwizard.kafka.security.SecurityFactory;
import io.dropwizard.kafka.serializer.SerializerFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.util.Duration;
import io.dropwizard.validation.MinDuration;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.record.CompressionType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public abstract class KafkaProducerFactory<K, V> extends KafkaClientFactory implements Discoverable {
    @Valid
    @NotNull
    @JsonProperty
    protected SerializerFactory keySerializer;

    @Valid
    @NotNull
    @JsonProperty
    protected SerializerFactory valueSerializer;

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

    @MinDuration(0)
    @JsonProperty
    protected Duration linger = Duration.milliseconds(0);

    @MinDuration(0)
    @JsonProperty
    protected Duration requestTimeout = Duration.seconds(30);

    @JsonProperty
    protected boolean enableIdempotence = false;

    @JsonProperty
    protected Optional<String> transactionalId = Optional.empty();

    public SerializerFactory getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(final SerializerFactory keySerializer) {
        this.keySerializer = keySerializer;
    }

    public SerializerFactory getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(final SerializerFactory valueSerializer) {
        this.valueSerializer = valueSerializer;
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

    public Duration getLinger() {
        return linger;
    }

    public void setLinger(final Duration linger) {
        this.linger = linger;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(final Duration requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public boolean isEnableIdempotence() {
        return enableIdempotence;
    }

    public void setEnableIdempotence(final boolean enableIdempotence) {
        this.enableIdempotence = enableIdempotence;
    }

    public Optional<String> getTransactionalId() {
        return transactionalId;
    }

    public void setTransactionalId(final Optional<String> transactionalId) {
        this.transactionalId = transactionalId;
    }

    protected Map<String, Object> createBaseKafkaConfigurations() {
        final Map<String, Object> config = new HashMap<>();

        config.putAll(keySerializer.build(true));
        config.putAll(valueSerializer.build(false));

        security.filter(SecurityFactory::isEnabled)
                .ifPresent(securityFactory -> config.putAll(securityFactory.build()));
        acks.ifPresent(acksValue -> config.put(ProducerConfig.ACKS_CONFIG, acksValue));
        retries.ifPresent(retriesValue -> config.put(ProducerConfig.RETRIES_CONFIG, retriesValue));
        maxInFlightRequestsPerConnection.ifPresent(maxInFlightRequestsPerConnectionValue ->
                config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequestsPerConnectionValue));
        maxPollBlockTime.ifPresent(maxPollBlockTimeValue ->
                config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxPollBlockTimeValue.toMilliseconds()));
        clientDNSLookup.ifPresent(clientIdValue -> config.put(CommonClientConfigs.CLIENT_DNS_LOOKUP_CONFIG, clientIdValue));
        clientId.ifPresent(clientIdValue -> config.put(CommonClientConfigs.CLIENT_ID_CONFIG, clientIdValue));
        transactionalId.ifPresent(transactionalIdValue -> config.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionalIdValue));

        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
        config.put(ProducerConfig.SEND_BUFFER_CONFIG, sendBufferBytes);
        config.put(ProducerConfig.RECEIVE_BUFFER_CONFIG, receiveBufferBytes);
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);
        config.put(ProducerConfig.LINGER_MS_CONFIG, (int) linger.toMilliseconds());
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, (int) requestTimeout.toMilliseconds());

        if (metricsEnabled) {
            config.put(DropwizardMetricsReporter.SHOULD_INCLUDE_TAGS_CONFIG, Boolean.toString(includeTaggedMetrics));
            config.put(CommonClientConfigs.METRIC_REPORTER_CLASSES_CONFIG, DropwizardMetricsReporter.class.getName());
            config.put(DropwizardMetricsReporter.METRICS_NAME_CONFIG, name);
        }

        return config;
    }

    protected void registerProducerHealthCheck(final HealthCheckRegistry healthChecks, final Producer<K, V> producer,
                                               final Collection<String> topics) {
        healthChecks.register(name, new KafkaProducerHealthCheck(producer, topics));
    }

    protected Producer<K, V> buildProducer(final Map<String, Object> config) {
        return new KafkaProducer<>(config);
    }

    protected void manageProducer(final LifecycleEnvironment lifecycle, final Producer<K, V> producer) {
        lifecycle.manage(new KafkaProducerManager(producer));
    }

    public Producer<K, V> build(final LifecycleEnvironment lifecycle,
                                final HealthCheckRegistry healthChecks,
                                final Collection<String> topics,
                                @Nullable final Tracing tracing) {
        return build(lifecycle, healthChecks, topics, tracing, Collections.emptyMap());
    }

    public abstract Producer<K, V> build(final LifecycleEnvironment lifecycle,
                                         final HealthCheckRegistry healthChecks,
                                         Collection<String> topics,
                                         @Nullable Tracing tracing,
                                         Map<String, Object> configOverrides);
}

package io.dropwizard.kafka;

import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.validation.ValidationMethod;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class BasicKafkaAdminClientFactory extends KafkaAdminClientFactory {
    private static final Logger log = LoggerFactory.getLogger(BasicKafkaAdminClientFactory.class);

    @Override
    public AdminClient build(final HealthCheckRegistry healthChecks, final LifecycleEnvironment lifecycle,
                             final Map<String, Object> configOverrides, final Collection<NewTopic> topics) {
        final Map<String, Object> config = new HashMap<>();

        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", bootstrapServers));

        config.put(AdminClientConfig.CLIENT_ID_CONFIG, clientId);

        config.put(AdminClientConfig.CLIENT_DNS_LOOKUP_CONFIG, clientDnsLookup.toString());
        config.put(AdminClientConfig.RECONNECT_BACKOFF_MS_CONFIG, reconnectBackoff.toMilliseconds());
        config.put(AdminClientConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, reconnectBackoffMax.toMilliseconds());
        config.put(AdminClientConfig.RETRIES_CONFIG, retries);
        config.put(AdminClientConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoff.toMilliseconds());
        config.put(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, connectionMaxIdle.toMilliseconds());
        config.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, (int) requestTimeout.toMilliseconds());
        config.put(AdminClientConfig.METADATA_MAX_AGE_CONFIG, metadataMaxAge.toMilliseconds());
        config.put(AdminClientConfig.SEND_BUFFER_CONFIG, sendBufferBytes);
        config.put(AdminClientConfig.RECEIVE_BUFFER_CONFIG, receiveBufferBytes);
        config.put(AdminClientConfig.METRIC_REPORTER_CLASSES_CONFIG, metricsReporters);
        config.put(AdminClientConfig.METRICS_NUM_SAMPLES_CONFIG, metricsSamples);
        config.put(AdminClientConfig.METRICS_SAMPLE_WINDOW_MS_CONFIG, metricsSampleWindow.toMilliseconds());
        config.put(AdminClientConfig.METRICS_RECORDING_LEVEL_CONFIG, metricsRecordingLevel.toString());

        if (security != null && security.isEnabled()) {
            config.putAll(security.build());
        }

        if (!requireNonNull(configOverrides).isEmpty()) {
            config.putAll(configOverrides);
        }

        final AdminClient adminClient = buildAdminClient(config);

        manageAdminClient(lifecycle, adminClient, topics);

        registerHealthCheck(healthChecks, adminClient);

        return adminClient;
    }

    @Override
    public AdminClient build(final HealthCheckRegistry healthChecks, final LifecycleEnvironment lifecycle,
                             final Map<String, Object> configOverrides) {
        List<NewTopic> newTopics = Collections.emptyList();
        if (topicCreationEnabled) {
             newTopics = topics.stream()
                    .map(KafkaTopicFactory::asNewTopic)
                    .collect(Collectors.toList());
        }
        return build(healthChecks, lifecycle, configOverrides, newTopics);
    }

    @ValidationMethod(message = "Bootstrap servers must not be null or empty in BasicKafkaAdminClientFactory " +
            "and topics must be defined if allowed to be created")
    public boolean isValidConfiguration() {
        final List<String> errors = new ArrayList<>();

        if (bootstrapServers != null && bootstrapServers.isEmpty()) {
            errors.add("bootstrapServers cannot be empty if basic type is configured");
        }

        if (topicCreationEnabled && topics.isEmpty()) {
            errors.add("topicCreationEnabled cannot be true with no topics defined");
        }

        if (!topicCreationEnabled && !topics.isEmpty()) {
            log.warn("topicCreationEnabled was set to false, but topics were defined");
        }

        if (!errors.isEmpty()) {
            final String errorMessage = String.join(System.lineSeparator(), errors);
            log.error("Failed to construct a basic Kafka cluster connection, due to the following errors:{}{}", System.lineSeparator(),
                    errorMessage);
            return false;
        }

        return true;
    }
}

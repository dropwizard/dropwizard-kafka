package io.dropwizard.kafka;

import brave.Tracing;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.ConfiguredBundle;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public abstract class KafkaAdminClientBundle<T extends Configuration> implements ConfiguredBundle<T> {
    private final Collection<NewTopic> topics;
    private final Map<String, Object> configOverrides;

    @Nullable
    private AdminClient adminClient;

    protected KafkaAdminClientBundle(final Collection<NewTopic> topics) {
        this(topics, Collections.emptyMap());
    }

    public KafkaAdminClientBundle(final Map<String, Object> configOverrides) {
        this(Collections.emptyList(), configOverrides);
    }

    protected KafkaAdminClientBundle(final Collection<NewTopic> topics, final Map<String, Object> configOverrides) {
        this.topics = Objects.requireNonNull(topics);
        this.configOverrides = Objects.requireNonNull(configOverrides);
    }

    @Override
    public void initialize(final Bootstrap<?> bootstrap) {
        // do nothing
    }

    @Override
    public void run(final T configuration, final Environment environment) throws Exception {
        final KafkaAdminClientFactory kafkaAdminClientFactory = requireNonNull(getKafkaAdminClientFactory(configuration));

        final Tracing tracing = Tracing.current();

        this.adminClient = kafkaAdminClientFactory.build(environment.lifecycle(), environment.healthChecks(), configOverrides, topics);
    }

    public abstract KafkaAdminClientFactory getKafkaAdminClientFactory(final T configuration);

    public AdminClient getAdminClient() {
        return requireNonNull(adminClient);
    }
}

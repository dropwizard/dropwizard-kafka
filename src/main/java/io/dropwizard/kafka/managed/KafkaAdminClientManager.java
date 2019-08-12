package io.dropwizard.kafka.managed;

import java.util.Collection;
import java.util.Set;

import io.dropwizard.lifecycle.Managed;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

public class KafkaAdminClientManager implements Managed {
    private static final Logger log = LoggerFactory.getLogger(KafkaAdminClientManager.class);

    private final AdminClient adminClient;
    private final String name;
    private final Collection<NewTopic> topics;

    public KafkaAdminClientManager(final AdminClient adminClient, final String name, final Collection<NewTopic> topics) {
        this.adminClient = requireNonNull(adminClient);
        this.name = requireNonNull(name);
        this.topics = topics;
    }

    @Override
    public void start() throws Exception {
        log.info("Starting adminClient for name={}", name);
        if (!this.topics.isEmpty()) {
            log.trace("Searching existing topics in cluster.");
            final Set<String> existingTopics = this.adminClient.listTopics().names().get();
            for (String t : existingTopics) {
                this.topics.removeIf(newTopic -> {
                    boolean match = newTopic.name().equals(t);
                    if (match) {
                        log.warn("Not attempting to re-create existing topic {}.", newTopic.name());
                    }
                    return match;
                });
            }
            this.adminClient.createTopics(this.topics);
        }
    }

    @Override
    public void stop() throws Exception {
        log.info("Shutting down adminClient for name={}", name);
        adminClient.close();
    }
}

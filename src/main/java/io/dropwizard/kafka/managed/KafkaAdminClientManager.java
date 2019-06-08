package io.dropwizard.kafka.managed;

import io.dropwizard.lifecycle.Managed;
import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

public class KafkaAdminClientManager implements Managed {
    private static final Logger log = LoggerFactory.getLogger(KafkaAdminClientManager.class);

    private final AdminClient adminClient;
    private final String name;

    public KafkaAdminClientManager(final AdminClient adminClient, final String name) {
        this.adminClient = requireNonNull(adminClient);
        this.name = requireNonNull(name);
    }

    @Override
    public void start() throws Exception {
        // do nothing
    }

    @Override
    public void stop() throws Exception {
        log.info("Shutting down adminClient for name={}", name);
        adminClient.close();
    }
}

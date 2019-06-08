package io.dropwizard.kafka.health;

import com.codahale.metrics.health.HealthCheck;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class KafkaAdminHealthCheck extends HealthCheck {
    private final AdminClient adminClient;
    private final String name;

    public KafkaAdminHealthCheck(final AdminClient adminClient, final String name) {
        this.adminClient = requireNonNull(adminClient);
        this.name = requireNonNull(name);
    }

    @Override
    protected Result check() throws Exception {
        try {
            final DescribeClusterResult response = adminClient.describeCluster();

            final boolean nodesNotEmpty = !response.nodes().get().isEmpty();
            final boolean clusterIdAvailable = response.clusterId() != null;
            final boolean aControllerExists = response.controller().get() != null;

            final List<String> errors = new ArrayList<>();

            if (!nodesNotEmpty) {
                errors.add("no nodes found for " + name);
            }

            if (!clusterIdAvailable) {
                errors.add("no cluster id available for " + name);
            }

            if (!aControllerExists) {
                errors.add("no active controller exists for " + name);
            }

            if (!errors.isEmpty()) {
                final String errorMessage = String.join(",", errors);
                return Result.unhealthy(errorMessage);
            }

            return Result.healthy();
        } catch (final Exception e) {
            return Result.unhealthy("Error describing Kafka Cluster name={}", name, e);
        }
    }
}

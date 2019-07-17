package io.dropwizard.kafka.integration;

import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.kafka.BasicKafkaAdminClientFactory;
import io.dropwizard.kafka.KafkaTopicFactory;

public class AdminClientIntegrationConfiguration extends Configuration {

    @NotNull
    @JsonProperty("admin")
    private BasicKafkaAdminClientFactory adminClientFactory;

    @NotNull
    @JsonProperty("topic")
    private KafkaTopicFactory topic;

    public BasicKafkaAdminClientFactory getAdminClientFactory() {
        return adminClientFactory;
    }

    public KafkaTopicFactory getTopic() {
        return topic;
    }

    public void setBootstrapServers(Set<String> bootstrapServers) {
        this.adminClientFactory.setBootstrapServers(bootstrapServers);
    }
}

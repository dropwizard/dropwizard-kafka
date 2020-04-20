package io.dropwizard.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class KafkaTopicFactory {

    @NotEmpty
    @JsonProperty
    private String name;

    @Min(1)
    @JsonProperty
    private int partitions;

    @Min(1)
    @JsonProperty
    private short replicationFactor;

    @NotNull
    @JsonProperty
    private Map<String, String> configs = Collections.emptyMap();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPartitions() {
        return partitions;
    }

    public void setPartitions(int partitions) {
        this.partitions = partitions;
    }

    public short getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(short replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public Map<String, String> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, String> configs) {
        this.configs = configs;
    }

    public NewTopic asNewTopic() {
        return new NewTopic(this.name, this.partitions, this.replicationFactor).configs(this.configs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KafkaTopicFactory that = (KafkaTopicFactory) o;
        return partitions == that.partitions &&
                replicationFactor == that.replicationFactor &&
                name.equals(that.name) &&
                Objects.equals(configs, that.configs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, partitions, replicationFactor, configs);
    }

}

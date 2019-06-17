package io.dropwizard.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.Discoverable;
import org.apache.kafka.clients.admin.NewTopic;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class KafkaTopicsFactory extends ArrayList<KafkaTopicsFactory.KafkaTopic> implements Discoverable {

    static class KafkaTopic {
        @NotEmpty
        @JsonProperty
        private String name;

        @Min(1)
        @JsonProperty
        private int partitions;

        @Min(1)
        @JsonProperty
        private short replicationFactor;

        @JsonProperty
        Map<String, String> configs = null;

        public NewTopic asNewTopic() {
            return new NewTopic(this.name, this.partitions, this.replicationFactor).configs(this.configs);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KafkaTopic that = (KafkaTopic) o;
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

    public NewTopic getNewTopic(int index) {
        return get(index).asNewTopic();
    }

    public Collection<NewTopic> getNewTopics() {
        return this.stream().map(KafkaTopic::asNewTopic).collect(Collectors.toSet());
    }
}

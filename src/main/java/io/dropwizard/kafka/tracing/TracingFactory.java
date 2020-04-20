package io.dropwizard.kafka.tracing;

import brave.Tracing;
import brave.kafka.clients.KafkaTracing;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class TracingFactory {
    @JsonProperty
    private boolean enabled = true;

    @JsonProperty
    private boolean writeB3SingleFormat = true;

    @JsonProperty
    private String remoteServiceName;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isWriteB3SingleFormat() {
        return writeB3SingleFormat;
    }

    public void setWriteB3SingleFormat(final boolean writeB3SingleFormat) {
        this.writeB3SingleFormat = writeB3SingleFormat;
    }

    public String getRemoteServiceName() {
        return remoteServiceName;
    }

    public void setRemoteServiceName(final String remoteServiceName) {
        this.remoteServiceName = remoteServiceName;
    }

    public Optional<KafkaTracing> build(final Tracing tracing) {
        if (tracing == null) {
            return Optional.empty();
        }

        if (!enabled) {
            return Optional.empty();
        }

        return Optional.of(KafkaTracing.newBuilder(tracing)
                .remoteServiceName(remoteServiceName)
                .build());
    }
}

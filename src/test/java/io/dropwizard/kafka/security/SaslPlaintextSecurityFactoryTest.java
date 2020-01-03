package io.dropwizard.kafka.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.junit.Test;

import javax.validation.Validator;
import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SaslPlaintextSecurityFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<SecurityFactory> configFactory =
            new YamlConfigurationFactory<>(SecurityFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildASaslPlaintextSecuritySetOfConfigs() throws Exception {
        final File yml = new File(Resources.getResource("yaml/security/sasl-plaintext-security.yaml").toURI());
        final SecurityFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(SaslPlaintextSecurityFactory.class);
        final Map<String, Object> config = factory.build();
        assertThat(config.get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))
                .isEqualTo(SecurityProtocol.SASL_PLAINTEXT.name());
        assertThat(config.get(SaslConfigs.SASL_MECHANISM))
                .isEqualTo("PLAIN");
        assertThat(config.get(SaslConfigs.SASL_JAAS_CONFIG))
                .isEqualTo("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"<username>\" password=\"<password>\";");
    }

    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(SaslPlaintextSecurityFactory.class);
    }
}

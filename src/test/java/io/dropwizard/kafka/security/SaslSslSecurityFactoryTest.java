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

import java.io.File;
import java.util.Map;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;


public class SaslSslSecurityFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<SecurityFactory> configFactory =
            new YamlConfigurationFactory<>(SecurityFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildASaslSslSecuritySetOfConfigs() throws Exception {
        final File yml = new File(Resources.getResource("yml/sasl-ssl-security.yml").toURI());
        final SecurityFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(SaslSslSecurityFactory.class);
        final Map<String, Object> config = factory.build();
        assertThat(config.get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))
                .isEqualTo(SecurityProtocol.SASL_SSL.name());
        assertThat(config.get(SslConfigs.SSL_PROTOCOL_CONFIG))
                .isEqualTo("TLSv1.2");
        assertThat(config.get(SaslConfigs.SASL_MECHANISM))
                .isEqualTo("PLAIN");
        assertThat(config.get(SaslConfigs.SASL_JAAS_CONFIG))
                .isEqualTo("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"<username>\" password=\"<password>\";");
    }

    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(SaslSslSecurityFactory.class);
    }
}

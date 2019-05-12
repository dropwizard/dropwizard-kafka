package io.dropwizard.kafka.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class SslSecurityFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<SecurityFactory> configFactory =
            new YamlConfigurationFactory<>(SecurityFactory.class, validator, objectMapper, "dw");

    @Test
    public void shouldBuildASslSecuritySetOfConfigs() throws Exception {
        final File yml = new File(Resources.getResource("yml/ssl-security.yml").toURI());
        final SecurityFactory factory = configFactory.build(yml);
        assertThat(factory)
                .isInstanceOf(SslSecurityFactory.class);
        final Map<String, Object> config = factory.build();
        assertThat(config.get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))
                .isEqualTo(SecurityProtocol.SSL.name());
        assertThat(config.get(SslConfigs.SSL_PROTOCOL_CONFIG))
                .isEqualTo("TLSv1.2");
    }

    @Test
    public void isDiscoverable() {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(SslSecurityFactory.class);
    }
}

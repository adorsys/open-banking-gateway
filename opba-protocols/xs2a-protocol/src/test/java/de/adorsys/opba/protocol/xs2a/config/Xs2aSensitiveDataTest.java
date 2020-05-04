package de.adorsys.opba.protocol.xs2a.config;

import de.adorsys.opba.protocol.api.services.scoped.RequestScopedServicesProvider;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableConfig;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableObjectMapper;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableProperties;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.RequestScopedStub;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

/**
 * Validates only module configuration.
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {FlowableConfig.class, Xs2aSensitiveDataTest.TestConfig.class}, webEnvironment = NONE)
public class Xs2aSensitiveDataTest {

    @Autowired
    private FlowableObjectMapper mapper;

    @Test
    @SneakyThrows
    void testPsuPasswordNotSerialized() {
        Xs2aContext context = new Xs2aContext();
        context.setRequestScoped(new RequestScopedStub());
        context.setSagaId("12345");
        context.setPsuPassword("PASSWORD");

        assertThat(mapper.writeValueAsString(context)).doesNotContain("psuPassword");
        assertThat(mapper.writeValueAsString(context)).doesNotContain("PASSWORD");
    }

    @Test
    @SneakyThrows
    void testLastScaChallengeNotSerialized() {
        Xs2aContext context = new Xs2aContext();
        context.setRequestScoped(new RequestScopedStub());
        context.setSagaId("123456");
        context.setLastScaChallenge("Challenge!");

        assertThat(mapper.writeValueAsString(context)).doesNotContain("lastScaChallenge");
        assertThat(mapper.writeValueAsString(context)).doesNotContain("Challenge!");
    }

    @Configuration
    public static class TestConfig {

        @Bean
        RequestScopedServicesProvider requestScopedServicesProvider() {
            return keyId -> new RequestScopedStub();
        }

        @Bean
        @ConditionalOnMissingBean(FlowableProperties.class)
        FlowableProperties flowableProperties() {
            return new FlowableProperties();
        }
    }
}



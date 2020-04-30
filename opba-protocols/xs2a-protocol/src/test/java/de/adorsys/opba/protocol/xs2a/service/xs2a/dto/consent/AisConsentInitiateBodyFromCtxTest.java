package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.context.ais.AccountListXs2aContext;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class AisConsentInitiateBodyFromCtxTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/consent_body_from_ctx_";

    @Autowired
    private AisConsentInitiateBody.FromCtx mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void aisConsentInitiateBodyMapperTest_non_empty_consent() {
        // Given
        Xs2aAisContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "ais_consent_non_empty_body_input.json", AccountListXs2aContext.class);
        AisConsentInitiateBody expected = fixtureProvider.getFromFile(PATH_PREFIX + "ais_consent_non_empty_body_output.json", AisConsentInitiateBody.class);

        // When
        AisConsentInitiateBody actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }

    @Test
    @SneakyThrows
    public void aisConsentInitiateBodyMapperTest_empty_consent() {
        // Given
        Xs2aAisContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "ais_consent_empty_body_input.json", AccountListXs2aContext.class);
        AisConsentInitiateBody expected = fixtureProvider.getFromFile(PATH_PREFIX + "ais_consent_empty_body_output.json", AisConsentInitiateBody.class);

        // When
        AisConsentInitiateBody actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }
}
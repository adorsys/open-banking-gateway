package de.adorsys.opba.protocol.xs2a.entrypoint.authorization.common;

import de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent;
import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AisConsentInitiateBody;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class UpdateAuthMapperAisMapperTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/consent_initiate_body_from_ctx_";

    @Autowired
    private UpdateAuthMapper.AisMapper mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void aisMapperTest() {
        // Given
        AisConsent mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "ais_consent_input.json", AisConsent.class);
        AisConsentInitiateBody expected = fixtureProvider.getFromFile(PATH_PREFIX + "ais_consent_output.json", AisConsentInitiateBody.class);

        // When
        AisConsentInitiateBody actual = mapper.map(mappingInput);

        // Then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }
}
package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class ConsentInitiateHeadersFromAisCtxTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/consent_initiate_headers_from_";

    @Autowired
    private ConsentInitiateHeaders.FromAisCtx mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void consentInitiateHeadersFromAisCtxMapperTest() {
        // Given
        Xs2aAisContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_ais_consent_input.json", Xs2aAisContext.class);
        ConsentInitiateHeaders expected = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_ais_consent_output.json", ConsentInitiateHeaders.class);

        // When
        ConsentInitiateHeaders actual = mapper.map(mappingInput);

        // Then
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }
}
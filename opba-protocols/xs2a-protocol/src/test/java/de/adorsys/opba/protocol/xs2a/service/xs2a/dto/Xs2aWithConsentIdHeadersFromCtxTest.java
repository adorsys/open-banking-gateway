package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class Xs2aWithConsentIdHeadersFromCtxTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/with_consentId_headers_from_ctx_";

    @Autowired
    private Xs2aWithConsentIdHeaders.FromCtx mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void xs2aWithConsentIdHeadersFromCtxMapperTest() {
        // Given
        Xs2aContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "xs2s_context_input.json", Xs2aContext.class);
        Xs2aWithConsentIdHeaders expected = fixtureProvider.getFromFile(PATH_PREFIX + "xs2s_context_output.json", Xs2aWithConsentIdHeaders.class);

        // When
        Xs2aWithConsentIdHeaders actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }
}
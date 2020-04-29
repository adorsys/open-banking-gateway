package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.RequestScopedStub;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class ProvideScaChallengeResultBodyFromCtxTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/provide_sca_challenge_result_body_from_";

    @Autowired
    private ProvideScaChallengeResultBody.FromCtx mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void provideScaChallengeResultBodyFromCtxMapperTest() {
        // Given
        Xs2aContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_context_input.json", Xs2aContext.class);
        mappingInput.setRequestScoped(new RequestScopedStub());
        mappingInput.setLastScaChallenge("test last sca challenge");
        ProvideScaChallengeResultBody expected = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_context_output.json", ProvideScaChallengeResultBody.class);

        // When
        ProvideScaChallengeResultBody actual = mapper.map(mappingInput);

        // Then
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }
}
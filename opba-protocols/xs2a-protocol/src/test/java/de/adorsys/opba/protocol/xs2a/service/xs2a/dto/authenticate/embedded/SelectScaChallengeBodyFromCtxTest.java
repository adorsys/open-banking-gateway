package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class SelectScaChallengeBodyFromCtxTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/select_sca_challenge_body_from_";

    @Autowired
    private SelectScaChallengeBody.FromCtx mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void selectScaChallengeBodyFromCtxMapperTest() {
        // Given
        Xs2aContext mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_context_input.json", Xs2aContext.class);
        SelectScaChallengeBody expected = fixtureProvider.getFromFile(PATH_PREFIX + "xs2a_context_output.json", SelectScaChallengeBody.class);

        // When
        SelectScaChallengeBody actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }
}
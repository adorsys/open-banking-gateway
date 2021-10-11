package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import de.adorsys.xs2a.adapter.api.model.SelectPsuAuthenticationMethod;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class SelectScaChallengeBodyToXs2aApiTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/select_psu_authentication_method_from_";

    @Autowired
    private SelectScaChallengeBody.ToXs2aApi mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void selectScaChallengeBodyToXs2aApiMapperTest() {
        // Given
        SelectScaChallengeBody mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "select_sca_challenge_body_input.json", SelectScaChallengeBody.class);
        SelectPsuAuthenticationMethod expected = fixtureProvider.getFromFile(PATH_PREFIX + "select_sca_challenge_body_output.json", SelectPsuAuthenticationMethod.class);

        // When
        SelectPsuAuthenticationMethod actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }
}
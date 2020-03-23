package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.util.UtilService;
import de.adorsys.xs2a.adapter.service.model.TransactionAuthorisation;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
class ProvideScaChallengeResultBodyToXs2aApiTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/transaction_authorisation_from_";

    @Autowired
    private ProvideScaChallengeResultBody.ToXs2aApi mapper;
    @Autowired
    private UtilService utilService;

    @Test
    @SneakyThrows
    public void provideScaChallengeResultBodyToXs2aApiMapperTest() {
        // Given
        ProvideScaChallengeResultBody mappingInput = utilService.getFromFile(PATH_PREFIX + "provide_sca_challenge_result_body_input.json", ProvideScaChallengeResultBody.class);
        TransactionAuthorisation expected = utilService.getFromFile(PATH_PREFIX + "provide_sca_challenge_result_body_output.json", TransactionAuthorisation.class);

        // When
        TransactionAuthorisation actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualToComparingFieldByField(actual);
    }
}
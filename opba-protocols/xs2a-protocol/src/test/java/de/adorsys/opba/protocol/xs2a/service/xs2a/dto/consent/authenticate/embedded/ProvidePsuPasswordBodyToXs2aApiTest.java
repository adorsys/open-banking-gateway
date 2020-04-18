package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthentication;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class ProvidePsuPasswordBodyToXs2aApiTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/update_psu_authentication_from_";

    @Autowired
    private ProvidePsuPasswordBody.ToXs2aApi mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void providePsuPasswordBodyToXs2aApiMapperTest() {
        // Given
        ProvidePsuPasswordBody mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "provide_psu_password_body_input.json", ProvidePsuPasswordBody.class);
        UpdatePsuAuthentication expected = fixtureProvider.getFromFile(PATH_PREFIX + "provide_psu_password_body_output.json", UpdatePsuAuthentication.class);

        // When
        UpdatePsuAuthentication actual = mapper.map(mappingInput);

        // Then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }
}
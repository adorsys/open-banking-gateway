package de.adorsys.opba.protocol.xs2a.domain.dto.forms;

import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.util.UtilService;
import de.adorsys.xs2a.adapter.service.model.AuthenticationObject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class FromAuthObjectTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/sca_method_from_auth_object_";

    @Autowired
    private UtilService utilService;
    @Autowired
    private ScaMethod.FromAuthObject mapper;

    @Test
    @SneakyThrows
    public void scaMethodMapperTest() {
        // Given
        AuthenticationObject mappingInput = utilService.getFromFile(PATH_PREFIX + "sca_method_input.json", AuthenticationObject.class);
        ScaMethod expected = utilService.getFromFile(PATH_PREFIX + "sca_method_output.json", ScaMethod.class);

        // When
        ScaMethod actual = mapper.map(mappingInput);

        // Then
        assertThat(expected).isEqualTo(actual);
    }
}
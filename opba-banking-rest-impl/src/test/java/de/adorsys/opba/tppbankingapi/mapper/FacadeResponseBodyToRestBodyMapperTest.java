package de.adorsys.opba.tppbankingapi.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import de.adorsys.opba.tppbankingapi.ais.model.generated.TransactionsResponse;
import de.adorsys.opba.tppbankingapi.controller.TppBankingApiAisController;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(classes = FacadeResponseBodyToRestBodyMapperTest.TestConfig.class)
public class FacadeResponseBodyToRestBodyMapperTest {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final String PATH_PREFIX = "mapper-test-fixtures/facade_to_rest_response_mapper_";

    @Autowired
    private TppBankingApiAisController.AccountListFacadeResponseBodyToRestBodyMapper accountsRestMapper;

    @Autowired
    private TppBankingApiAisController.TransactionsFacadeResponseBodyToRestBodyMapper transactionsRestMapper;

    @Test
    @SneakyThrows
    void accountsMapperTest() {
        AccountListBody mappingInput = getFromFile(PATH_PREFIX + "accounts_input.json", AccountListBody.class);
        AccountList mappingResult = accountsRestMapper.map(mappingInput);

        AccountList expected = getFromFile(PATH_PREFIX + "accounts_output.json", AccountList.class);
        assertThat(expected).isEqualToComparingFieldByField(mappingResult);
    }

    @Test
    void transactionsMapperTest() {
        TransactionsResponseBody mappingInput = getFromFile(PATH_PREFIX + "transactions_input.json",
                TransactionsResponseBody.class);
        TransactionsResponse mappingResult = transactionsRestMapper.map(mappingInput);

        TransactionsResponse expected = getFromFile(PATH_PREFIX + "transactions_output.json",
                TransactionsResponse.class);
        assertThat(expected).isEqualToComparingFieldByField(mappingResult);
    }

    @SneakyThrows
    private <T> T getFromFile(String path, Class<T> valueType) {
        return JSON_MAPPER.readValue(Resources.getResource(path), valueType);
    }

    @Configuration
    @ComponentScan(basePackages = "de.adorsys.opba.tppbankingapi.mapper.generated")
    public static class TestConfig {
    }
}

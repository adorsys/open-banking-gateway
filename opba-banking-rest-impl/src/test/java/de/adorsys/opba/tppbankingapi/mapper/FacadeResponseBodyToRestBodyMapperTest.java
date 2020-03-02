package de.adorsys.opba.tppbankingapi.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListDetailBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import de.adorsys.opba.tppbankingapi.ais.model.generated.TransactionsResponse;
import de.adorsys.opba.tppbankingapi.controller.TppBankingApiAisController;
import de.adorsys.opba.tppbankingapi.mapper.generated.TppBankingApiAisController$AccountListFacadeResponseBodyToRestBodyMapperImpl;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = FacadeResponseBodyToRestBodyMapperTest.TestConfig.class)
public class FacadeResponseBodyToRestBodyMapperTest {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    public static final String PATH_PREFIX = "mapper-test-fixtures/facade_to_rest_response_";

    @Autowired
    private TppBankingApiAisController.AccountListFacadeResponseBodyToRestBodyMapper accountsRestMapper;

    @Autowired
    private TppBankingApiAisController.TransactionsFacadeResponseBodyToRestBodyMapper transactionsRestMapper;

    private static final String IBAN = "DE122342343243";
    private static final String DELETED = "DELETED";

    @Test
    public void testMapping() {
        AccountListDetailBody account = AccountListDetailBody.builder()
                .iban(IBAN)
                .status(DELETED)
                .build();

        List<AccountListDetailBody> accountDetails = new ArrayList<>();
        accountDetails.add(account);
        AccountListBody facadeEntity = AccountListBody.builder().accounts(accountDetails).build();

        AccountList restEntity = new TppBankingApiAisController$AccountListFacadeResponseBodyToRestBodyMapperImpl().map(facadeEntity);
        assertEquals(IBAN, restEntity.getAccounts().get(0).getIban());
        assertEquals(DELETED, restEntity.getAccounts().get(0).getStatus().name());
    }

    @Test
    @SneakyThrows
    void accountsMapperTest() {
        String inputUrl = PATH_PREFIX + "accounts_mapper_input.json";
        AccountListBody inputObject = getFromFile(inputUrl, AccountListBody.class);
        AccountList outputObject = accountsRestMapper.map(inputObject);

        JSONObject outputJson = new JSONObject(outputObject);
        JSONObject expected = getRESTData(PATH_PREFIX + "accounts_mapper_output.json");
        JSONAssert.assertEquals(expected, outputJson, JSONCompareMode.STRICT);
    }

    @Test

    void transactionsMapperTest() {
        String inputUrl = PATH_PREFIX + "transactions_mapper_input.json";
        TransactionsResponseBody inputObject = getFromFile(inputUrl, TransactionsResponseBody.class);
        TransactionsResponse outputObject = transactionsRestMapper.map(inputObject);

        JSONObject outputJson = new JSONObject(outputObject);
        JSONObject expected = getRESTData(PATH_PREFIX + "transactions_mapper_output.json");
        JSONAssert.assertEquals(expected, outputJson, JSONCompareMode.STRICT);
    }

    @SneakyThrows
    private <T> T getFromFile(String path, Class<T> valueType) {
        return JSON_MAPPER.readValue(Resources.getResource(path), valueType);
    }

    @SneakyThrows
    private JSONObject getRESTData(String path) {
        String file = Resources.getResource(path).getPath();
        String content = new String(Files.readAllBytes(Paths.get(file)));
        return new JSONObject(content);
    }

    @Configuration
    @ComponentScan(basePackages = "de.adorsys.opba.tppbankingapi.mapper.generated")
    public static class TestConfig {
    }
}

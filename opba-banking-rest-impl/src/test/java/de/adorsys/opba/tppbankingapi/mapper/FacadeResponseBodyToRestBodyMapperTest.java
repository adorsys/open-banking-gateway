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
import de.adorsys.opba.tppbankingapi.mapper.generated.TppBankingApiAisController$AccountListFacadeResponseBodyToRestBodyMapperImpl;
import de.adorsys.opba.tppbankingapi.mapper.generated.TppBankingApiAisController$TransactionsFacadeResponseBodyToRestBodyMapperImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = {TppBankingApiAisController$AccountListFacadeResponseBodyToRestBodyMapperImpl.class,
                            TppBankingApiAisController$TransactionsFacadeResponseBodyToRestBodyMapperImpl.class})
public class FacadeResponseBodyToRestBodyMapperTest {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    private TppBankingApiAisController$AccountListFacadeResponseBodyToRestBodyMapperImpl accountsRestMapper;

    @Autowired
    private TppBankingApiAisController$TransactionsFacadeResponseBodyToRestBodyMapperImpl transactionsRestMapper;

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
        URL inputUrl = Resources.getResource("mapperTestFixtures/facade_to_rest_response_accounts_mapper_input.json");
        AccountListBody inputObject = JSON_MAPPER.readValue(inputUrl, AccountListBody.class);
        AccountList outputObject = accountsRestMapper.map(inputObject);

        URL outputUrl = Resources.getResource("mapperTestFixtures/facade_to_rest_response_accounts_mapper_output.json");
        AccountList expectedObject = JSON_MAPPER.readValue(outputUrl, AccountList.class);

        assertThat(expectedObject).isEqualToComparingFieldByField(outputObject);
    }

    @Test
    @SneakyThrows
    void transactionsMapperTest() {
        URL inputUrl = Resources.getResource("mapperTestFixtures/facade_to_rest_response_transactions_mapper_input.json");
        TransactionsResponseBody inputObject = JSON_MAPPER.readValue(inputUrl, TransactionsResponseBody.class);
        TransactionsResponse outputObject = transactionsRestMapper.map(inputObject);

        URL outputUrl = Resources.getResource("mapperTestFixtures/facade_to_rest_response_transactions_mapper_output.json");
        TransactionsResponse expectedObject = JSON_MAPPER.readValue(outputUrl, TransactionsResponse.class);

        assertThat(expectedObject).isEqualToComparingFieldByField(outputObject);
    }
}

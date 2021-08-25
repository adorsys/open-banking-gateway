package de.adorsys.opba.protocol.xs2a.entrypoint;

import com.google.common.io.Resources;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import de.adorsys.opba.protocol.xs2a.util.XmlTransactionsParser;
import de.adorsys.xs2a.adapter.api.model.AccountList;
import de.adorsys.xs2a.adapter.api.model.TransactionsResponse200Json;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(classes = MapperTestConfig.class)
public class Xs2AToFacadeMapperTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/xs2a_to_facade_response_mapper_";

    @Autowired
    private Xs2aResultBodyExtractor.Xs2aToFacadeMapper mapper;

    @Autowired
    private XmlTransactionsParser xmlTransactionsParser;

    @Autowired
    AccountStatementMapper xmlMapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    void accountsMapperTest() {
        AccountList mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "accounts_input.json", AccountList.class);
        AccountListBody mappingResult = mapper.map(mappingInput);

        AccountListBody expected = fixtureProvider.getFromFile(PATH_PREFIX + "accounts_output.json", AccountListBody.class);
        assertThat(expected).isEqualToComparingFieldByField(mappingResult);
    }

    @Test
    @SneakyThrows
    void transactionsMapperTest() {
        TransactionsResponse200Json mappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "transactions_input.json",
                TransactionsResponse200Json.class);

        TransactionsResponseBody mappingResult = mapper.map(mappingInput);

        TransactionsResponseBody expected = fixtureProvider.getFromFile(PATH_PREFIX + "transactions_output.json",
                                                                        TransactionsResponseBody.class);
        assertThat(expected).isEqualToComparingFieldByField(mappingResult);
    }

    @Test
    @SneakyThrows
    public void testCamt() {
        String camt = Resources.toString(Resources.getResource("mapper-test-fixtures/camt_multibanking.xml"), StandardCharsets.UTF_8);
        TransactionsResponseBody loadBookingsResponse = xmlTransactionsParser.camtStringToLoadBookingsResponse(camt);
        assertNotNull(loadBookingsResponse);
        assertThat(loadBookingsResponse.getTransactions().getBooked().size()).withFailMessage("Wrong count of bookings").isEqualTo(4);
    }

    @Test
    @SneakyThrows
    public void testCamtSparkasse() {
        String camt = Resources.toString(Resources.getResource("mapper-test-fixtures/camt_sparkasse.xml"), StandardCharsets.UTF_8);
        TransactionsResponseBody loadBookingsResponse = xmlTransactionsParser.camtStringToLoadBookingsResponse(camt);
        assertNotNull(loadBookingsResponse);
        assertThat(loadBookingsResponse.getTransactions().getBooked().size()).withFailMessage("Wrong count of bookings").isEqualTo(1);
    }
}

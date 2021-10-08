package de.adorsys.opba.protocol.xs2a.entrypoint;

import com.google.common.io.Resources;
import de.adorsys.opba.protocol.api.dto.context.Context;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.AccountReport;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.entrypoint.parsers.XmlTransactionsParser;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import de.adorsys.xs2a.adapter.api.model.AccountList;
import de.adorsys.xs2a.adapter.api.model.TransactionsResponse200Json;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MapperTestConfig.class)
public class Xs2AToFacadeMapperTest {
    public static final String PATH_PREFIX = "mapper-test-fixtures/xs2a_to_facade_response_mapper_";
    public static final String XML_PATH_PREFIX = "xml-mapper-test-fixtures/";

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

        ListTransactionsRequest transactionsRequest = fixtureProvider.getFromFile(PATH_PREFIX + "list_transactions_request_input.json", ListTransactionsRequest.class);
        ServiceContext<ListTransactionsRequest> context = ServiceContext.<ListTransactionsRequest>builder()
                .ctx(Context.<ListTransactionsRequest>builder()
                        .serviceSessionId(transactionsRequest.getFacadeServiceable().getServiceSessionId())
                        .request(transactionsRequest)
                        .build())
                .build();

        TransactionsResponseBody mappingResult = mapper.map(mappingInput, context);

        TransactionsResponseBody expected = fixtureProvider.getFromFile(PATH_PREFIX + "transactions_output.json",
                                                                        TransactionsResponseBody.class);
        assertThat(expected).isEqualToComparingFieldByField(mappingResult);
    }

    @Test
    @SneakyThrows
    void transactionsWithPagingMapperTest() {
        TransactionsResponse200Json transactionsResponseMappingInput = fixtureProvider.getFromFile(PATH_PREFIX + "transactions_with_paging_input.json",
                TransactionsResponse200Json.class);

        ListTransactionsRequest transactionsRequest = fixtureProvider.getFromFile(PATH_PREFIX + "list_transactions_request_with_paging_input.json", ListTransactionsRequest.class);
        ServiceContext<ListTransactionsRequest> contextMappingInput = ServiceContext.<ListTransactionsRequest>builder()
                .ctx(Context.<ListTransactionsRequest>builder()
                        .serviceSessionId(transactionsRequest.getFacadeServiceable().getServiceSessionId())
                        .request(transactionsRequest)
                        .build())
                .build();

        TransactionsResponseBody mappingResult = mapper.map(transactionsResponseMappingInput, contextMappingInput);

        TransactionsResponseBody expected = fixtureProvider.getFromFile(PATH_PREFIX + "transactions_with_paging_output.json",
                TransactionsResponseBody.class);
        assertThat(expected).isEqualToComparingFieldByField(mappingResult);
    }

    @Test
    @SneakyThrows
    void testCamt() {
        String camt = Resources.toString(Resources.getResource(XML_PATH_PREFIX + "camt-multibanking.xml"), StandardCharsets.UTF_8);
        TransactionsResponseBody loadBookingsResponse = xmlTransactionsParser.camtStringToLoadBookingsResponse(camt);

        assertThat(loadBookingsResponse.getTransactions().getBooked().size()).withFailMessage("Wrong count of bookings").isEqualTo(4);

        TransactionsResponseBody expected = fixtureProvider.getFromFile(XML_PATH_PREFIX + "multibanking-output.json",
                TransactionsResponseBody.class);

        // ignore rawTransactions field
        loadBookingsResponse = loadBookingsResponse.toBuilder()
                .transactions(AccountReport.builder()
                        .booked(loadBookingsResponse.getTransactions().getBooked())
                        .pending(loadBookingsResponse.getTransactions().getPending())
                        .build())
                .build();

        assertThat(loadBookingsResponse).isEqualToComparingFieldByField(expected);
    }

    @Test
    @SneakyThrows
    void testCamtSparkasse() {
        String camt = Resources.toString(Resources.getResource(XML_PATH_PREFIX + "camt-sparkasse.xml"), StandardCharsets.UTF_8);
        TransactionsResponseBody mappingResult = xmlTransactionsParser.camtStringToLoadBookingsResponse(camt);

        assertThat(mappingResult.getTransactions().getBooked().size()).withFailMessage("Wrong count of bookings").isEqualTo(2);

        TransactionsResponseBody expected = fixtureProvider.getFromFile(XML_PATH_PREFIX + "sparkasse-output.json",
                                                                        TransactionsResponseBody.class);
        // ignore rawTransactions field
        mappingResult = mappingResult.toBuilder()
            .transactions(AccountReport.builder()
                              .booked(mappingResult.getTransactions().getBooked())
                              .pending(mappingResult.getTransactions().getPending())
                              .build())
            .build();

        assertThat(mappingResult).isEqualToComparingFieldByField(expected);
    }
}

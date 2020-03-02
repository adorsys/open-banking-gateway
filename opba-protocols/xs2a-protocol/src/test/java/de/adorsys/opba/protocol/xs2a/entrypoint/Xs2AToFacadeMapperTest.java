package de.adorsys.opba.protocol.xs2a.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import de.adorsys.xs2a.adapter.service.model.TransactionsReport;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Xs2AToFacadeMapperTest.TestConfig.class)
public class Xs2AToFacadeMapperTest {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    @Autowired
    private Xs2aResultBodyExtractor.Xs2aToFacadeMapper mapper;

    @Test
    @SneakyThrows
    void accountsMapperTest() {
        URL inputUrl = Resources.getResource("mapper-test-fixtures/xs2a_to_facade_response_mapper_accounts_input.json");
        AccountListHolder inputObject = JSON_MAPPER.readValue(inputUrl, AccountListHolder.class);
        AccountListBody outputObject = mapper.map(inputObject);

        URL outputUrl = Resources.getResource("mapper-test-fixtures/xs2a_to_facade_response_mapper_accounts_output.json");
        AccountListBody expectedObject = JSON_MAPPER.readValue(outputUrl, AccountListBody.class);

        assertThat(expectedObject).isEqualToComparingFieldByField(outputObject);
    }

    @Test
    @SneakyThrows
    void transactionsMapperTest() {
        URL inputUrl = Resources.getResource("mapper-test-fixtures/xs2a_to_facade_response_mapper_transactions_input.json");
        TransactionsReport inputObject = JSON_MAPPER.readValue(inputUrl, TransactionsReport.class);
        TransactionsResponseBody outputObject = mapper.map(inputObject);

        URL outputUrl = Resources.getResource("mapper-test-fixtures/xs2a_to_facade_response_mapper_transactions_output.json");
        TransactionsResponseBody expectedObject = JSON_MAPPER.readValue(outputUrl, TransactionsResponseBody.class);

        assertThat(expectedObject).isEqualToComparingFieldByField(outputObject);
    }

    @Configuration
    @ComponentScan(basePackages = "de.adorsys.opba.protocol.xs2a.service.mappers.generated")
    public static class TestConfig {
    }
}

package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.db.config.EnableBankingPersistence;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ValidationErrorResult;
import de.adorsys.opba.protocol.xs2a.EnableXs2aProtocol;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled // FIXME https://github.com/adorsys/open-banking-gateway/issues/253
@ActiveProfiles("test")
@SpringBootTest(classes = ListTransactionsServiceTest.TestConfig.class)
class ListTransactionsServiceTest {

    @Autowired
    private ListTransactionsService listTransactionsService;

    @Test
    @SneakyThrows
    void testXs2aWired() {
        assertThat(listTransactionsService.execute(
                ListTransactionsRequest.builder()
                        .facadeServiceable(
                                FacadeServiceableRequest.builder()
                                        .bankId("53c47f54-b9a4-465a-8f77-bc6cd5f0cf46")
                                        .build()
                        ).build()
                ).get()
        ).isInstanceOf(ValidationErrorResult.class);
    }

    @EnableXs2aProtocol
    @EnableBankingPersistence
    @SpringBootApplication(scanBasePackages = "de.adorsys.opba.protocol.facade")
    public static class TestConfig {
    }
}

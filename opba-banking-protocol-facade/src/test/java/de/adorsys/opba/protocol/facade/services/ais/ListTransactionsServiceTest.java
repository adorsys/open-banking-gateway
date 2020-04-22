package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.db.config.EnableBankingPersistence;
import de.adorsys.opba.protocol.api.ais.ListTransactions;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.AuthorizationRequiredResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeStartAuthorizationResult;
import de.adorsys.opba.protocol.facade.services.DbDropper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Note: This test keeps DB in dirty state - doesn't cleanup after itself.
 */
@ActiveProfiles("test")
/*
As we redefine list accounts for adorsys-sandbox bank to sandbox customary one
(and it doesn't make sense to import sandbox module here) moving it back to plain xs2a bean:
 */
@SuppressWarnings("CPD-START") // Same steps are used, but that's fine for readability
@Sql(statements = "UPDATE opb_bank_protocol SET protocol_bean_name = 'xs2aListTransactions' WHERE protocol_bean_name = 'xs2aSandboxListTransactions'")
@SpringBootTest(classes = ListTransactionsServiceTest.TestConfig.class)
class ListTransactionsServiceTest extends DbDropper {

    @Autowired
    private ListTransactionsService listTransactionsService;

    @MockBean(name = "xs2aListTransactions")
    private ListTransactions listTransactions;

    @SuppressWarnings("PMD.AvoidUsingHardCodedIP") // This is a test
    @Test
    @SneakyThrows
    void testXs2aWired() {
        when(listTransactions.execute(any(ServiceContext.class)))
                .thenReturn(CompletableFuture.completedFuture(new AuthorizationRequiredResult<>(URI.create("http://example.com"), null)));

        assertThat(listTransactionsService.execute(
                ListTransactionsRequest.builder()
                        .facadeServiceable(
                                FacadeServiceableRequest.builder()
                                        .uaContext(UserAgentContext.builder().psuIpAddress("1.1.1.1").build())
                                        .requestId(UUID.randomUUID())
                                        .bankId("53c47f54-b9a4-465a-8f77-bc6cd5f0cf46")
                                        .sessionPassword("123")
                                        .authorization("SUPER-FINTECH-ID")
                                        .fintechUserId("user1@fintech.com")
                                        .fintechRedirectUrlOk("http://google.com")
                                        .fintechRedirectUrlNok("http://microsoft.com")
                                        .build()
                        ).build()
                ).get()
        ).isInstanceOf(FacadeStartAuthorizationResult.class);
    }

    @EnableBankingPersistence
    @SpringBootApplication(scanBasePackages = "de.adorsys.opba.protocol.facade")
    public static class TestConfig {
    }
}

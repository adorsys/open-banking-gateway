package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.db.config.EnableBankingPersistence;
import de.adorsys.opba.protocol.api.ais.ListAccounts;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
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

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Note: This test keeps DB in dirty state - doesn't cleanup after itself.
 */
@SuppressWarnings("CPD-START") // Same steps are used, but that's fine for readability
@ActiveProfiles("test")
@SpringBootTest(classes = ListAccountsServiceTest.TestConfig.class)
class ListAccountsServiceTest extends DbDropper {

    @Autowired
    private ListAccountsService listAccountsService;

    @MockBean(name = "xs2aListAccounts")
    private ListAccounts listAccounts;

    @SuppressWarnings("PMD.AvoidUsingHardCodedIP") // This is a test
    @Test
    @SneakyThrows
    void testXs2aWired() {
        when(listAccounts.execute(any(ServiceContext.class)))
                .thenReturn(CompletableFuture.completedFuture(new AuthorizationRequiredResult<>(URI.create("http://example.com"), null)));

        assertThat(listAccountsService.execute(
                ListAccountsRequest.builder()
                        .facadeServiceable(
                                FacadeServiceableRequest.builder()
                                        .uaContext(UserAgentContext.builder().psuIpAddress("1.1.1.1").build())
                                        .requestId(UUID.randomUUID())
                                        .bankProfileId(UUID.fromString("53c47f54-b9a4-465a-8f77-bc6cd5f0cf46"))
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

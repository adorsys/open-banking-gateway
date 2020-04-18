package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.db.config.EnableBankingPersistence;
import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeStartAuthorizationResult;
import de.adorsys.opba.protocol.xs2a.EnableXs2aProtocol;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Note: This test keeps DB in dirty state - doesn't cleanup after itself.
 */
@SuppressWarnings("CPD-START") // Same steps are used, but that's fine for readability
@ActiveProfiles("test")
@SpringBootTest(classes = ListAccountsServiceTest.TestConfig.class)
class ListAccountsServiceTest {

    @Autowired
    private ListAccountsService listAccountsService;

    @SuppressWarnings("PMD.AvoidUsingHardCodedIP") // This is a test
    @Test
    @SneakyThrows
    void testXs2aWired() {
        assertThat(listAccountsService.execute(
                ListAccountsRequest.builder()
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

    @EnableXs2aProtocol
    @EnableBankingPersistence
    @SpringBootApplication(scanBasePackages = "de.adorsys.opba.protocol.facade")
    public static class TestConfig {
    }
}

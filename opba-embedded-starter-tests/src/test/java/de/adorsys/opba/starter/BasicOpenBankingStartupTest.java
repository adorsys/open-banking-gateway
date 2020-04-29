package de.adorsys.opba.starter;

import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.protocol.xs2a.entrypoint.ais.Xs2aListAccountsEntrypoint;
import de.adorsys.opba.protocol.xs2a.entrypoint.ais.Xs2aSandboxListTransactionsEntrypoint;
import de.adorsys.opba.starter.config.FintechRequestSigningTestConfig;
import io.restassured.RestAssured;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.AIS_ACCOUNTS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.AIS_TRANSACTIONS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.ANTON_BRUECKNER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.withDefaultHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * This is a very basic test to ensure application starts up and components are bundled properly.
 * Protocols are tested in their own packages exhaustively.
 */
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {OpenBankingEmbeddedApplication.class, FintechRequestSigningTestConfig.class}, webEnvironment = RANDOM_PORT)
class BasicOpenBankingStartupTest {

    @SpyBean
    private Xs2aListAccountsEntrypoint xs2aListAccountsEntrypoint;

    @SpyBean
    private Xs2aSandboxListTransactionsEntrypoint xs2aListTransactionsEntrypoint;

    @LocalServerPort
    private int serverPort;

    @Autowired
    private RequestSigningService requestSigningService;

    @BeforeEach
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost:" + serverPort;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        config = config().redirect(redirectConfig().followRedirects(false));
    }

    @Test
    @SneakyThrows
    void testXs2aProtocolIsWiredForSandboxAccountList() {
        withDefaultHeaders(ANTON_BRUECKNER, requestSigningService)
                .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
            .when()
                .get(AIS_ACCOUNTS_ENDPOINT)
            .then()
                .statusCode(HttpStatus.ACCEPTED.value());

        verify(xs2aListAccountsEntrypoint).execute(any());
    }

    @Test
    @SneakyThrows
    void testXs2aProtocolIsWiredForSandboxTransactionList() {
        withDefaultHeaders(ANTON_BRUECKNER, requestSigningService)
                .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
            .when()
                .get(AIS_TRANSACTIONS_ENDPOINT, "ACCOUNT-1")
            .then()
                .statusCode(HttpStatus.ACCEPTED.value());

        verify(xs2aListTransactionsEntrypoint).execute(any());
    }
}

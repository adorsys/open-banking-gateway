package de.adorsys.opba.starter;

import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.api.security.requestsigner.OpenBankingDataToSignProvider;
import de.adorsys.opba.protocol.xs2a.entrypoint.ais.Xs2aListAccountsEntrypoint;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.CommonGivenStages;
import de.adorsys.opba.starter.config.FintechRequestSigningTestConfig;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
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

import java.time.Instant;
import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.BANK_PROFILE_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_DATA_PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_REDIRECT_URL_NOK;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_REDIRECT_URL_OK;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_USER_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_TIMESTAMP_UTC;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AIS_ACCOUNTS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.ANTON_BRUECKNER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.DEFAULT_FINTECH_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.FINTECH_REDIR_NOK;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.FINTECH_REDIR_OK;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.SANDBOX_BANK_PROFILE_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * This is a very basic test to ensure application starts up and components are bundled properly.
 * Protocols are tested in their own packages exhaustively.
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles(profiles = {"test", "test-separate-db"})  // Use clean DB as may collide
@SpringBootTest(classes = {OpenBankingEmbeddedApplication.class, FintechRequestSigningTestConfig.class}, webEnvironment = RANDOM_PORT)
class FintechPasswordApplyTest {

    @SpyBean
    private Xs2aListAccountsEntrypoint xs2aListAccountsEntrypoint;

    @LocalServerPort
    private int serverPort;

    @Autowired
    private RequestSigningService signingService;

    @BeforeEach
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost:" + serverPort;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        config = config().redirect(redirectConfig().followRedirects(false));
        RestAssured.replaceFiltersWith(new CommonGivenStages.RequestSigner(signingService, new OpenBankingDataToSignProvider()));
    }

    @Test
    void testFintechPasswordIsValidated() {
        xs2aAccountList(HttpStatus.ACCEPTED, "qwerty");
        xs2aAccountList(HttpStatus.ACCEPTED, "qwerty");
        xs2aAccountList(HttpStatus.UNAUTHORIZED, "not-qwerty");

        verify(xs2aListAccountsEntrypoint, times(2)).execute(any());
    }

    @Test
    @Deprecated // To be removed with Service-Session-Password header
    void testFintechPasswordIsValidatedWithServiceSessionPassword() {
        xs2aAccountListUsingServiceSessionPassword(HttpStatus.ACCEPTED, "qwerty");
        xs2aAccountListUsingServiceSessionPassword(HttpStatus.ACCEPTED, "qwerty");
        xs2aAccountListUsingServiceSessionPassword(HttpStatus.UNAUTHORIZED, "not-qwerty");

        verify(xs2aListAccountsEntrypoint, times(2)).execute(any());
    }

    private void xs2aAccountListUsingServiceSessionPassword(HttpStatus expected, String fintechPassword) {
        headersWithoutIpAddress(ANTON_BRUECKNER, SANDBOX_BANK_PROFILE_ID, UUID.randomUUID(), Instant.now())
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                    .header("Service-Session-Password", fintechPassword)
                .when()
                    .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                    .statusCode(expected.value());
    }

    private void xs2aAccountList(HttpStatus expected, String fintechPassword) {
        headersWithoutIpAddress(ANTON_BRUECKNER, SANDBOX_BANK_PROFILE_ID, UUID.randomUUID(), Instant.now())
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                    .header(FINTECH_DATA_PASSWORD, fintechPassword)
                .when()
                    .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                    .statusCode(expected.value());
    }

    private static RequestSpecification headersWithoutIpAddress(String fintechUserId, String bankProfileId, UUID xRequestId, Instant xTimestampUtc) {
        return RestAssured
                .given()
                    .header(BANK_PROFILE_ID, bankProfileId)
                    .header(FINTECH_REDIRECT_URL_OK, FINTECH_REDIR_OK)
                    .header(FINTECH_REDIRECT_URL_NOK, FINTECH_REDIR_NOK)
                    .header(FINTECH_USER_ID, fintechUserId)
                    .header(FINTECH_ID, DEFAULT_FINTECH_ID)
                    .header(X_REQUEST_ID, xRequestId.toString())
                    .header(X_TIMESTAMP_UTC, xTimestampUtc.toString());
    }
}

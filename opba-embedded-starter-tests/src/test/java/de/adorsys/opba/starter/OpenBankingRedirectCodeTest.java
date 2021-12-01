package de.adorsys.opba.starter;

import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.api.security.requestsigner.OpenBankingDataToSignProvider;
import de.adorsys.opba.protocol.xs2a.entrypoint.ais.Xs2aListAccountsEntrypoint;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.CommonGivenStages;
import de.adorsys.opba.starter.config.FintechRequestSigningTestConfig;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.jetbrains.annotations.Nullable;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_PSU_AUTHENTICATION_REQUIRED;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.PIS_ANONYMOUS_LOGIN_USER_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.RequestCommon.REDIRECT_CODE_QUERY;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AIS_ACCOUNTS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.ANTON_BRUECKNER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withAccountsHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * This is a very basic test to ensure application starts up and components are bundled properly.
 * Protocols are tested in their own packages exhaustively.
 */
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {OpenBankingEmbeddedApplication.class, FintechRequestSigningTestConfig.class}, webEnvironment = RANDOM_PORT)
class OpenBankingRedirectCodeTest {

    @SpyBean
    private Xs2aListAccountsEntrypoint xs2aListAccountsEntrypoint;

    @Autowired
    private RequestSigningService signingService;

    @LocalServerPort
    private int serverPort;

    @BeforeEach
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost:" + serverPort;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        config = config().redirect(redirectConfig().followRedirects(false));
        RestAssured.replaceFiltersWith(new CommonGivenStages.RequestSigner(signingService, new OpenBankingDataToSignProvider()));
    }

    @Test
    void testXs2aProtocolIsWiredForSandboxAccountList() {
        var resp = xs2aAccountList(HttpStatus.ACCEPTED);
        xs2aLoginToSession(getRedirectCode(resp), resp.header(SERVICE_SESSION_ID));

        verify(xs2aListAccountsEntrypoint).execute(any());
    }

    @Nullable
    private String getRedirectCode(ExtractableResponse<Response> resp) {
        return UriComponentsBuilder.fromUriString(resp.header(LOCATION)).build().getQueryParams().getFirst(REDIRECT_CODE_QUERY);
    }

    private ExtractableResponse<Response> xs2aAccountList(HttpStatus expected) {
        return withAccountsHeaders(ANTON_BRUECKNER)
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                    .header(X_PSU_AUTHENTICATION_REQUIRED, "false")
                .when()
                    .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                    .statusCode(expected.value())
                    .extract();
    }

    private void xs2aLoginToSession(String fintechUserTempPassword, String serviceSessionId) {
        ExtractableResponse<Response> response = RestAssured
                .given()
                .header(X_REQUEST_ID, UUID.randomUUID().toString())
                .queryParam(REDIRECT_CODE_QUERY, fintechUserTempPassword)
                .contentType(APPLICATION_JSON_VALUE)
                .when()
                .post(PIS_ANONYMOUS_LOGIN_USER_ENDPOINT, serviceSessionId)
                .then()
                .statusCode(ACCEPTED.value())
                .extract();
    }

//    protected ExtractableResponse<Response> xs2aStartConsentAuthorization(String uriPath, String body, HttpStatus status, String serviceSessionId) {
//        return RestAssured
//                .given()
//                .header(X_REQUEST_ID, UUID.randomUUID().toString())
//                .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
//                .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
//                .queryParam(REDIRECT_CODE_QUERY, redirectCode)
//                .queryParam(X_XSRF_TOKEN_QUERY, redirectCode)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(body)
//                .when()
//                .post(uriPath, serviceSessionId)
//                .then()
//                .statusCode(status.value())
//                .extract();
//    }
}

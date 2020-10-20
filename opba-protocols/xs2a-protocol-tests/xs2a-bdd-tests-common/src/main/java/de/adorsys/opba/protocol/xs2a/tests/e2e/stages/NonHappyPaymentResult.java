package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.RestAssured;

import java.util.UUID;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.RequestCommon.REDIRECT_CODE_QUERY;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.DENY_CONSENT_AUTH_ENDPOINT;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class NonHappyPaymentResult<SELF extends NonHappyPaymentResult<SELF>> extends Stage<SELF> {

    public static final String DENY_PAYMENT_ENDPOINT = DENY_CONSENT_AUTH_ENDPOINT;

    @ProvidedScenarioState
    protected String authSessionCookie;

    @ProvidedScenarioState
    protected String paymentServiceSessionId;

    @ProvidedScenarioState
    protected String redirectCode;

    public SELF user_anton_brueckner_requests_payment_denial_and_he_is_redirected_back_to_fintech_ok() {
        RestAssured
                .given()
                .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                .header(X_REQUEST_ID, UUID.randomUUID().toString())
                .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                .queryParam(REDIRECT_CODE_QUERY, redirectCode)
                .contentType(APPLICATION_JSON_VALUE)
                .body("{}")
                .when()
                .post(DENY_PAYMENT_ENDPOINT, paymentServiceSessionId)
                .then()
                .statusCode(ACCEPTED.value())
                .header(LOCATION, "http://localhost:4444/redirect-after-consent-denied");

        return self();
    }

    public SELF user_anton_brueckner_requests_payment_denial_and_it_is_impossible_as_payment_is_authorized() {
        RestAssured
                .given()
                .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                .header(X_REQUEST_ID, UUID.randomUUID().toString())
                .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                .queryParam(REDIRECT_CODE_QUERY, redirectCode)
                .contentType(APPLICATION_JSON_VALUE)
                .body("{}")
                .when()
                .post(DENY_PAYMENT_ENDPOINT, paymentServiceSessionId)
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.value())
                .body("message", is("Unable to drop payment after it was authorized"));

        return self();
    }
}

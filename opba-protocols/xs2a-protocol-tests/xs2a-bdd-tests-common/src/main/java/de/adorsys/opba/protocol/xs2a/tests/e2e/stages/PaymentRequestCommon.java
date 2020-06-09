package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.google.common.collect.ImmutableMap;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.LOGIN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.ANTON_BRUECKNER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.INITIATE_PAYMENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.PIS_LOGIN_USER_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.SEPA_PAYMENT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.withPaymentHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.REDIRECT_CODE;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static org.springframework.http.HttpHeaders.LOCATION;

@Slf4j
@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class PaymentRequestCommon<SELF extends PaymentRequestCommon<SELF>> extends Stage<SELF> {

    public static final String REDIRECT_CODE_QUERY = "redirectCode";

    @ProvidedScenarioState
    protected String authSessionCookie;

    @ProvidedScenarioState
    protected String redirectUriToGetUserParams;

    @ProvidedScenarioState
    protected String serviceSessionId;

    @ProvidedScenarioState
    protected String redirectCode;

    @Autowired
    private RequestSigningService requestSigningService;

    public SELF fintech_calls_initiate_payment_for_anton_brueckner() {
        ExtractableResponse<Response> response = withPaymentHeaders(ANTON_BRUECKNER, requestSigningService, OperationType.PIS)
                 .when()
                    .post(INITIATE_PAYMENT_ENDPOINT, SEPA_PAYMENT)
                 .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                 .extract();

        updateServiceSessionId(response);
        updateRedirectCode(response);
        updateNextPaymentAuthorizationUrl(response);
        return self();
    }

    public SELF user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(String username, String password) {
        String fintechUserTempPassword = UriComponentsBuilder
                                                 .fromHttpUrl(redirectUriToGetUserParams).build()
                                                 .getQueryParams()
                                                 .getFirst(REDIRECT_CODE_QUERY);

        ExtractableResponse<Response> response = RestAssured
                 .given()
                     .header(X_REQUEST_ID, UUID.randomUUID().toString())
                     .contentType(MediaType.APPLICATION_JSON_VALUE)
                     .queryParam(REDIRECT_CODE_QUERY, fintechUserTempPassword)
                     .body(ImmutableMap.of(LOGIN, username, PASSWORD, password))
                 .when()
                     .post(PIS_LOGIN_USER_ENDPOINT, serviceSessionId)
                 .then()
                     .statusCode(HttpStatus.ACCEPTED.value())
                     .extract();

        this.authSessionCookie = response.cookie(AUTHORIZATION_SESSION_KEY);
        return self();
    }

    protected void updateServiceSessionId(ExtractableResponse<Response> response) {
        this.serviceSessionId = response.header(SERVICE_SESSION_ID);
    }


    protected void updateRedirectCode(ExtractableResponse<Response> response) {
        this.redirectCode = response.header(REDIRECT_CODE);
    }

    protected void updateNextPaymentAuthorizationUrl(ExtractableResponse<Response> response) {
        this.redirectUriToGetUserParams = response.header(LOCATION);
    }
}

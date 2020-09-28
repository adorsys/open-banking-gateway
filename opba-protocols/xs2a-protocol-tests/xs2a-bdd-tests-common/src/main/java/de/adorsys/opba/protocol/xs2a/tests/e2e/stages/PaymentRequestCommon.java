package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.google.common.collect.ImmutableMap;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.consentapi.model.generated.ConsentAuth;
import de.adorsys.opba.consentapi.model.generated.SinglePayment;
import de.adorsys.opba.protocol.xs2a.tests.e2e.LocationExtractorUtil;
import de.adorsys.xs2a.adapter.api.model.PaymentProduct;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.ResourceUtil.readResource;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.ANTON_BRUECKNER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.AUTHORIZE_PAYMENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.GET_PAYMENT_AUTH_STATE;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.INITIATE_PAYMENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.LOGIN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.PIS_ANONYMOUS_LOGIN_USER_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.PIS_LOGIN_USER_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.SEPA_PAYMENT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.withPaymentHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.withPaymentInfoHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AUTHORIZE_CONSENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.GET_CONSENT_AUTH_STATE;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.MAX_MUSTERMAN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.PIS_SINGLE_PAYMENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withDefaultHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.REDIRECT_CODE;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class PaymentRequestCommon<SELF extends PaymentRequestCommon<SELF>> extends RequestCommon<SELF> {

    public SELF fintech_calls_initiate_payment_for_anton_brueckner(String bankId) {
        String body = readResource("restrecord/tpp-ui-input/params/anton-brueckner-single-sepa-payment.json");
        ExtractableResponse<Response> response = withPaymentHeaders(ANTON_BRUECKNER, bankId, true)
                .contentType(APPLICATION_JSON_VALUE)
                .body(body)
            .when()
                .post(INITIATE_PAYMENT_ENDPOINT, SEPA_PAYMENT)
            .then()
                .statusCode(ACCEPTED.value())
                .extract();

        updateServiceSessionId(response);
        updateRedirectCode(response);
        updateNextPaymentAuthorizationUrl(response);
        return self();
    }

    public SELF fintech_calls_initiate_payment_for_anton_brueckner() {
        String body = readResource("restrecord/tpp-ui-input/params/anton-brueckner-single-sepa-payment.json");
        ExtractableResponse<Response> response = withPaymentHeaders(ANTON_BRUECKNER)
                 .contentType(APPLICATION_JSON_VALUE)
                 .body(body)
             .when()
                 .post(INITIATE_PAYMENT_ENDPOINT, SEPA_PAYMENT)
             .then()
                 .statusCode(ACCEPTED.value())
                 .extract();

        updateServiceSessionId(response);
        updateRedirectCode(response);
        updateNextPaymentAuthorizationUrl(response);
        return self();
    }

    public SELF fintech_calls_initiate_payment_for_anton_brueckner_with_anonymous_allowed() {
        String body = readResource("restrecord/tpp-ui-input/params/anton-brueckner-single-sepa-payment.json");
        ExtractableResponse<Response> response = withPaymentHeaders(ANTON_BRUECKNER, false)
                .contentType(APPLICATION_JSON_VALUE)
                .body(body)
            .when()
                .post(INITIATE_PAYMENT_ENDPOINT, SEPA_PAYMENT)
            .then()
                .statusCode(ACCEPTED.value())
                .extract();

        updateServiceSessionId(response);
        updateRedirectCode(response);
        updateNextPaymentAuthorizationUrl(response);
        return self();
    }

    public SELF fintech_calls_initiate_payment_for_max_musterman() {
        String body = readResource("restrecord/tpp-ui-input/params/max-musterman-single-sepa-payment.json");
        ExtractableResponse<Response> response = withPaymentHeaders(MAX_MUSTERMAN)
                .contentType(APPLICATION_JSON_VALUE)
                .body(body)
            .when()
                .post(PIS_SINGLE_PAYMENT_ENDPOINT, PaymentProduct.SEPA_CREDIT_TRANSFERS.toString())
            .then()
                .statusCode(ACCEPTED.value())
                .extract();

        updateServiceSessionId(response);
        updateRedirectCode(response);
        updateNextPaymentAuthorizationUrl(response);
        return self();
    }

    public SELF fintech_calls_initiate_payment_for_max_musterman_with_anonymous_allowed() {
        String body = readResource("restrecord/tpp-ui-input/params/max-musterman-single-sepa-payment.json");
        ExtractableResponse<Response> response = withPaymentHeaders(MAX_MUSTERMAN, false)
                .contentType(APPLICATION_JSON_VALUE)
                .body(body)
             .when()
                .post(PIS_SINGLE_PAYMENT_ENDPOINT, PaymentProduct.SEPA_CREDIT_TRANSFERS.toString())
             .then()
                .statusCode(ACCEPTED.value())
                .extract();

        updateServiceSessionId(response);
        updateRedirectCode(response);
        updateNextPaymentAuthorizationUrl(response);
        return self();
    }

    public SELF user_logged_in_into_opba_as_anonymous_user_with_credentials_using_fintech_supplied_url() {
        String fintechUserTempPassword = UriComponentsBuilder
                .fromHttpUrl(redirectUriToGetUserParams).build()
                .getQueryParams()
                .getFirst(REDIRECT_CODE_QUERY);

        ExtractableResponse<Response> response = RestAssured
            .given()
                .header(X_REQUEST_ID, UUID.randomUUID().toString())
                .queryParam(REDIRECT_CODE_QUERY, fintechUserTempPassword)
                .contentType(APPLICATION_JSON_VALUE)
            .when()
                .post(PIS_ANONYMOUS_LOGIN_USER_ENDPOINT, paymentServiceSessionId)
            .then()
                .statusCode(ACCEPTED.value())
            .extract();

        this.authSessionCookie = response.cookie(AUTHORIZATION_SESSION_KEY);
        return self();
    }

    public SELF user_logged_in_into_opba_pis_as_opba_user_with_credentials_using_fintech_supplied_url(String username, String password) {
        String fintechUserTempPassword = UriComponentsBuilder
                                                 .fromHttpUrl(redirectUriToGetUserParams).build()
                                                 .getQueryParams()
                                                 .getFirst(REDIRECT_CODE_QUERY);

        ExtractableResponse<Response> response = RestAssured
             .given()
                 .header(X_REQUEST_ID, UUID.randomUUID().toString())
                 .queryParam(REDIRECT_CODE_QUERY, fintechUserTempPassword)
                 .contentType(APPLICATION_JSON_VALUE)
                 .body(ImmutableMap.of(LOGIN, username, PASSWORD, password))
             .when()
                .post(PIS_LOGIN_USER_ENDPOINT, paymentServiceSessionId)
             .then()
                 .statusCode(ACCEPTED.value())
                 .extract();

        assertThat(response.header(LOCATION)).contains("/pis");
        this.authSessionCookie = response.cookie(AUTHORIZATION_SESSION_KEY);
        return self();
    }

    public SELF user_anton_brueckner_provided_initial_parameters_to_authorize_initiation_payment() {
        ExtractableResponse<Response> response = RestAssured
             .given()
                 .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                 .header(X_REQUEST_ID, UUID.randomUUID().toString())
                 .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                 .queryParam(REDIRECT_CODE_QUERY, redirectCode)
                 .contentType(APPLICATION_JSON_VALUE)
                 .body(readResource("restrecord/tpp-ui-input/params/anton-brueckner-payments-authorize.json"))
             .when()
                .post(AUTHORIZE_PAYMENT_ENDPOINT, paymentServiceSessionId)
             .then()
                .statusCode(ACCEPTED.value())
                 .extract();

        this.responseContent = response.body().asString();
        updateNextPaymentAuthorizationUrl(response);
        updateServiceSessionId(response);
        updateRedirectCode(response);

        return self();
    }

    public SELF user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp() {
        ExtractableResponse<Response> response = withPaymentInfoHeaders(ANTON_BRUECKNER)
                 .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                 .queryParam(REDIRECT_CODE_QUERY, redirectCode)
             .when()
                .get(GET_PAYMENT_AUTH_STATE, paymentServiceSessionId)
             .then()
                .statusCode(HttpStatus.OK.value())
                .extract();

        assertThatResponseContainsAntonBruecknersSinglePayment(response);

        updateNextPaymentAuthorizationUrl(response);
        updateServiceSessionId(response);
        updateRedirectCode(response);
        return self();
    }

    public SELF user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok(String challengeType) {
        assertThat(this.redirectUriToGetUserParams).contains("sca-result").contains(challengeType).doesNotContain("wrong=true");
        ExtractableResponse<Response> response = max_musterman_provides_sca_challenge_result();
        assertThat(LocationExtractorUtil.getLocation(response)).contains("pis").contains("consent-result");
        return self();
    }

    public SELF user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok() {
        return user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok("/EMAIL");
    }

    public SELF user_max_musterman_provided_initial_parameters_to_make_payment() {
        ExtractableResponse<Response> response = startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-account-all-accounts-consent.json")
        );

        assertThat(response.header(LOCATION)).contains("/pis/");
        return self();
    }

    public SELF user_max_musterman_provided_password_to_embedded_authorization() {
        assertThat(this.redirectUriToGetUserParams).contains("authenticate").doesNotContain("wrong=true");
        max_musterman_provides_password();
        updateAvailableScas();
        return self();
    }

    public SELF user_max_musterman_selected_sca_challenge_type_email2_to_embedded_authorization() {
        provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
                selectedScaBody("EMAIL:max.musterman2@mail.de"),
                ACCEPTED
        );
        return self();
    }

    public SELF user_max_musterman_selected_sca_challenge_type_photo_otp_to_embedded_authorization() {
        provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
                selectedScaBody("PHOTO_OTP:photo_otp"),
                ACCEPTED
        );
        return self();
    }

    public SELF ui_can_read_image_data_from_obg(String user) {
        ExtractableResponse<Response> response = withDefaultHeaders(user)
                                                            .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                                                            .queryParam(REDIRECT_CODE_QUERY, redirectCode)
                                                         .when()
                                                            .get(GET_CONSENT_AUTH_STATE, paymentServiceSessionId)
                                                         .then()
                                                            .statusCode(HttpStatus.OK.value())
                                                            .extract();

        assertThatResponseContainsCorrectChallengeData(response, "restrecord/tpp-ui-input/params/max-musterman-embedded-payment-challenge-data.json");
        updateServiceSessionId(response);
        updateRedirectCode(response);
        return self();
    }

    public SELF user_anton_brueckner_provided_initial_parameters_to_authorize_initiation_payment_without_cookie_unauthorized() {
        RestAssured
            .given()
                    .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .queryParam(REDIRECT_CODE_QUERY, redirectCode)
                    .contentType(APPLICATION_JSON_VALUE)
                    .body(readResource("restrecord/tpp-ui-input/params/anton-brueckner-psu-id-parameter.json"))
            .when()
                    .post(AUTHORIZE_PAYMENT_ENDPOINT, paymentServiceSessionId)
            .then()
                    .statusCode(UNAUTHORIZED.value())
                    .extract();

        return self();
    }

    public SELF user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp_without_cookie_unauthorized() {
        withPaymentInfoHeaders(ANTON_BRUECKNER)
                .queryParam(REDIRECT_CODE_QUERY, redirectCode)
            .when()
                .get(GET_PAYMENT_AUTH_STATE, paymentServiceSessionId)
            .then()
                .statusCode(UNAUTHORIZED.value())
                .extract();

        return self();
    }

    protected void updateServiceSessionId(ExtractableResponse<Response> response) {
        this.paymentServiceSessionId = response.header(SERVICE_SESSION_ID);
    }

    protected void updateRedirectCode(ExtractableResponse<Response> response) {
        this.redirectCode = response.header(REDIRECT_CODE);
    }

    protected void updateNextPaymentAuthorizationUrl(ExtractableResponse<Response> response) {
        this.redirectUriToGetUserParams = LocationExtractorUtil.getLocation(response);
    }

    @SneakyThrows
    private void assertThatResponseContainsAntonBruecknersSinglePayment(ExtractableResponse<Response> response) {
        ConsentAuth authResponse = JSON_MAPPER
                                                .readValue(response.body().asString(), ConsentAuth.class);

        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getSinglePayment())
                .isEqualTo(JSON_MAPPER.readValue(readResource("restrecord/tpp-ui-input/params/anton-brueckner-single-payment-response.json"),
                                                 SinglePayment.class));
    }

    protected ExtractableResponse<Response> startInitialInternalConsentAuthorization(String uriPath, String resourceData) {
        ExtractableResponse<Response> response =
                startInitialInternalConsentAuthorization(uriPath, resourceData, ACCEPTED);
        updateServiceSessionId(response);
        updateRedirectCode(response);

        return response;
    }

    protected ExtractableResponse<Response> provideParametersToBankingProtocolWithBody(String uriPath, String body, HttpStatus status) {
        return provideParametersToBankingProtocolWithBody(uriPath, body, status, paymentServiceSessionId);
    }

    protected ExtractableResponse<Response> provideGetConsentAuthStateRequest() {
        return provideGetConsentAuthStateRequest(paymentServiceSessionId);
    }
}

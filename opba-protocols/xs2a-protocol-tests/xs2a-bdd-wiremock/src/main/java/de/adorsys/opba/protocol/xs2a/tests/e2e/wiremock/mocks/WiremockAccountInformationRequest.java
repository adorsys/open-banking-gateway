package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.LocationExtractorUtil;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationRequestCommon;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.awaitility.Durations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.ResourceUtil.readResource;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AIS_ACCOUNTS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.ANTON_BRUECKNER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AUTHORIZE_CONSENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withAccountsHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withAccountsHeadersComputeIpAddress;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withSignatureHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.COMPUTE_PSU_IP_ADDRESS;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.PSU_IP_ADDRESS;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.TPP_NOK_REDIRECT_URI;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.TPP_REDIRECT_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class WiremockAccountInformationRequest<SELF extends WiremockAccountInformationRequest<SELF>> extends AccountInformationRequestCommon<SELF> {

    @ExpectedScenarioState
    private WireMockServer wireMock;

    public SELF open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test() {
        return open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test(0);
    }

    public SELF open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test(int consentCreationRequestIndex) {
        extractRedirectOkUriSentByOpbaFromWiremock(consentCreationRequestIndex);
        ExtractableResponse<Response> response = withSignatureHeaders(RestAssured
                .given()
                    .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie))
                .when()
                    .get(redirectOkUri)
                .then()
                    .statusCode(HttpStatus.SEE_OTHER.value())
                    .extract();

        assertThat(LocationExtractorUtil.getLocation(response)).contains("ais").contains("consent-result");

        return self();
    }

    public SELF open_banking_redirect_from_aspsp_with_static_oauth2_code_to_exchange_to_token(String code) {
        extractRedirectOkUriSentByOpbaFromWiremock();
        ExtractableResponse<Response> response = RestAssured
                .given()
                    .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                .when()
                    .get(redirectOkUri + "?code=" + code)
                .then()
                    .statusCode(HttpStatus.SEE_OTHER.value())
                .extract();

        updateRedirectCode(response);
        return self();
    }

    public SELF open_banking_redirect_from_aspsp_with_static_oauth2_code_to_exchange_to_token_ing(String code) {
        extractRedirectOkUriSentByOpbaFromWiremockIng();
        ExtractableResponse<Response> response = RestAssured
            .given()
            .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
            .when()
            .get(redirectOkUri + "?code=" + code)
            .then()
            .statusCode(HttpStatus.SEE_OTHER.value())
            .extract();

        updateRedirectCode(response);
        return self();
    }

    public SELF open_banking_redirect_from_aspsp_not_ok_webhook_called_for_api_test() {
        LoggedRequest consentInitiateRequest = await().atMost(Durations.TEN_SECONDS)
                                                       .until(() ->
                                                                      wireMock.findAll(postRequestedFor(urlMatching("/v1/consents.*"))), it -> !it.isEmpty()
                                                       ).get(0);
        this.redirectNotOkUri = consentInitiateRequest.getHeader(TPP_NOK_REDIRECT_URI);
        ExtractableResponse<Response> response = withSignatureHeaders(RestAssured
                    .given()
                        .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie))
                    .when()
                        .get(redirectNotOkUri)
                    .then()
                        .statusCode(HttpStatus.SEE_OTHER.value())
                        .extract();

        assertThat(LocationExtractorUtil.getLocation(response)).contains("redirect-after-consent-denied");

        return self();
    }

    public SELF fintech_calls_list_accounts_for_anton_brueckner_ip_address_compute() {
        ExtractableResponse<Response> response = withAccountsHeadersComputeIpAddress(ANTON_BRUECKNER)
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                    .header(COMPUTE_PSU_IP_ADDRESS, true)
                .when()
                    .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                .extract();
        updateServiceSessionId(response);
        updateRedirectCode(response);
        updateNextConsentAuthorizationUrl(response);
        return self();
    }

    public SELF fintech_calls_list_accounts_for_anton_brueckner_no_ip_address() {
        ExtractableResponse<Response> response = withAccountsHeaders(ANTON_BRUECKNER)// FIX HEADERS
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                    .header(COMPUTE_PSU_IP_ADDRESS, false)
                .when()
                    .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                .extract();
        updateServiceSessionId(response);
        updateRedirectCode(response);
        updateNextConsentAuthorizationUrl(response);
        return self();
    }

    public SELF user_anton_brueckner_provided_initial_parameters_but_without_psu_id_to_list_accounts_with_all_accounts_consent() {
        String body = readResource("restrecord/tpp-ui-input/params/unknown-user-all-accounts-consent.json");

        ExtractableResponse<Response> response = RestAssured
                 .given()
                     .header(X_REQUEST_ID, UUID.randomUUID().toString())
                     .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                     .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                     .queryParam(X_XSRF_TOKEN_QUERY, redirectCode)
                     .contentType(MediaType.APPLICATION_JSON_VALUE)
                     .body(body)
                 .when()
                    .post(AUTHORIZE_CONSENT_ENDPOINT, serviceSessionId)
                 .then()
                     .statusCode(HttpStatus.ACCEPTED.value())
                     .extract();

        assertThat(LocationExtractorUtil.getLocation(response)).contains("redirectCode").doesNotContain("consent-result");

        this.responseContent = response.body().asString();
        this.redirectUriToGetUserParams = LocationExtractorUtil.getLocation(response);
        updateRedirectCode(response);
        updateServiceSessionId(response);
        updateRedirectCode(response);

        return self();
    }

    public SELF user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent_with_ip_address_check() {
        String antonBruecknerParametersBody = readResource("restrecord/tpp-ui-input/params/anton-brueckner-account-all-accounts-consent.json");

        return user_provided_initial_parameters_in_body_to_list_accounts_with_all_accounts_consent_with_ip_address_check(antonBruecknerParametersBody);
    }

    public SELF user_anton_brueckner_provided_psu_id_parameter_to_list_accounts_with_all_accounts_consent_with_ip_address_check() {
        String antonBruecknerPsuIdBody = readResource("restrecord/tpp-ui-input/params/anton-brueckner-in-extras.json");

        return user_provided_initial_parameters_in_body_to_list_accounts_with_all_accounts_consent_with_ip_address_check(antonBruecknerPsuIdBody);
    }

    public SELF user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_dedicated_consent() {
        String antonBruecknerParametersBody = readResource("restrecord/tpp-ui-input/params/anton-brueckner-dedicated-account-consent.json");

        return user_provided_initial_parameters_in_body_to_list_accounts_with_all_accounts_consent_with_ip_address_check(antonBruecknerParametersBody);
    }

    public SELF user_provided_initial_parameters_in_body_to_list_accounts_with_all_accounts_consent_with_ip_address_check(String body) {
        ExtractableResponse<Response> response = RestAssured
                 .given()
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                    .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                    .queryParam(X_XSRF_TOKEN_QUERY, redirectCode)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                 .when()
                    .post(AUTHORIZE_CONSENT_ENDPOINT, serviceSessionId)
                 .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .extract();

        LoggedRequest loggedRequest = await().atMost(Durations.TEN_SECONDS)
                  .until(() ->
                                 wireMock.findAll(postRequestedFor(urlMatching("/v1/consents.*"))), it -> !it.isEmpty()
                  ).get(0);

        assertThat(loggedRequest.getHeader(PSU_IP_ADDRESS)).isNotEmpty();
        assertThat(LocationExtractorUtil.getLocation(response)).contains("ais").contains("to-aspsp-redirection");

        this.responseContent = response.body().asString();
        this.redirectUriToGetUserParams = LocationExtractorUtil.getLocation(response);
        updateRedirectCode(response);
        updateServiceSessionId(response);
        updateRedirectCode(response);

        return self();
    }

    public SELF user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_wrong_ibans() {
        String body = readResource("restrecord/tpp-ui-input/params/anton-brueckner-account-wrong-ibans.json");

        ExtractableResponse<Response> response = RestAssured
                 .given()
                     .header(X_REQUEST_ID, UUID.randomUUID().toString())
                     .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                     .queryParam(X_XSRF_TOKEN_QUERY, redirectCode)
                     .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                     .contentType(MediaType.APPLICATION_JSON_VALUE)
                     .body(body)
                 .when()
                    .post(AUTHORIZE_CONSENT_ENDPOINT, serviceSessionId)
                 .then().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                 .extract();

        this.responseContent = response.body().asString();

        return self();
    }

    public SELF got_500_http_error_body() {
        assertThat(this.responseContent).contains("");
        return self();
    }

    public SELF open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test_without_cookie_unauthorized() {
        extractRedirectOkUriSentByOpbaFromWiremock();

        withSignatureHeaders(RestAssured.given())
                 .when()
                    .get(redirectOkUri)
                 .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                 .extract();

        return self();
    }

    private void extractRedirectOkUriSentByOpbaFromWiremock() {
        extractRedirectOkUriSentByOpbaFromWiremock(0);
    }

    private void extractRedirectOkUriSentByOpbaFromWiremock(int consentCreationRequestIndex) {
        LoggedRequest consentInitiateRequest = await().atMost(Durations.TEN_SECONDS)
                .until(() ->
                        wireMock.findAll(postRequestedFor(urlMatching("/v1/consents.*"))), it -> !it.isEmpty()
                ).get(consentCreationRequestIndex);
        this.redirectOkUri = consentInitiateRequest.getHeader(TPP_REDIRECT_URI);
    }

    private void extractRedirectOkUriSentByOpbaFromWiremockIng() {
        LoggedRequest consentInitiateRequest = await().atMost(Durations.TEN_SECONDS)
            .until(() ->
                       wireMock.findAll(getRequestedFor(urlMatching("/oauth2/authorization-server-url.*"))), it -> !it.isEmpty()
            ).get(0);
        this.redirectOkUri = consentInitiateRequest.getQueryParams().get("redirect_uri").firstValue();
    }
}

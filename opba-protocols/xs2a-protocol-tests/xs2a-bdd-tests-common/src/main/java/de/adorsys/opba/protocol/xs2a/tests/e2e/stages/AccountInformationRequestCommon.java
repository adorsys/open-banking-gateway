package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.consentapi.model.generated.InlineResponse200;
import de.adorsys.opba.consentapi.model.generated.ScaUserData;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.ResourceUtil.readResource;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.AIS_ACCOUNTS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.AIS_LOGIN_USER_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.AIS_TRANSACTIONS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.ANTON_BRUECKNER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.AUTHORIZE_CONSENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.DENY_CONSENT_AUTH_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.FINTECH_REDIR_NOK;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.GET_CONSENT_AUTH_STATE;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.LOGIN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.MAX_MUSTERMAN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.withDefaultHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static de.adorsys.opba.restapi.shared.HttpHeaders.REDIRECT_CODE;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;

@Slf4j
@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class AccountInformationRequestCommon<SELF extends AccountInformationRequestCommon<SELF>> extends Stage<SELF> {

    public static final String REDIRECT_CODE_QUERY = "redirectCode";

    @ProvidedScenarioState
    protected String authSessionCookie;

    @ProvidedScenarioState
    protected String redirectUriToGetUserParams;

    @ProvidedScenarioState
    protected String serviceSessionId;

    @ProvidedScenarioState
    protected String redirectCode;

    @ProvidedScenarioState
    @SuppressWarnings("PMD.UnusedPrivateField") // used by AccountListResult!
    protected String redirectOkUri;

    @ProvidedScenarioState
    @SuppressWarnings("PMD.UnusedPrivateField") // used by AccountListResult!
    protected String responseContent;

    @ScenarioState
    private List<ScaUserData> availableScas;

    public SELF fintech_calls_list_accounts_for_anton_brueckner() {
        ExtractableResponse<Response> response = withDefaultHeaders(ANTON_BRUECKNER)
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
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

    public SELF fintech_calls_list_accounts_for_max_musterman() {
        ExtractableResponse<Response> response = withDefaultHeaders(MAX_MUSTERMAN)
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
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

    public SELF fintech_calls_list_transactions_for_anton_brueckner(String resourceId) {
        ExtractableResponse<Response> response = withDefaultHeaders(ANTON_BRUECKNER)
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                .when()
                    .get(AIS_TRANSACTIONS_ENDPOINT, resourceId)
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                .extract();

        updateServiceSessionId(response);
        updateRedirectCode(response);
        updateNextConsentAuthorizationUrl(response);
        return self();
    }

    public SELF fintech_calls_list_transactions_for_max_musterman() {
        return fintech_calls_list_transactions_for_max_musterman("oN7KTVuJSVotMvPPPavhVo");
    }

    public SELF fintech_calls_list_transactions_for_max_musterman(String resourceId) {
        ExtractableResponse<Response> response = withDefaultHeaders(MAX_MUSTERMAN)
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                .when()
                    .get(AIS_TRANSACTIONS_ENDPOINT, resourceId)
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .extract();

        updateServiceSessionId(response);
        updateRedirectCode(response);
        updateNextConsentAuthorizationUrl(response);
        return self();
    }

    public SELF user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(String username, String password) {
        String fintechUserTempPassword = UriComponentsBuilder
                .fromHttpUrl(redirectUriToGetUserParams).build()
                .getQueryParams()
                .getFirst(REDIRECT_CODE_QUERY);

        ExtractableResponse<Response> response = RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .queryParam(REDIRECT_CODE_QUERY, fintechUserTempPassword)
                    .body(ImmutableMap.of(LOGIN, username, PASSWORD, password))
                .when()
                    .post(AIS_LOGIN_USER_ENDPOINT, serviceSessionId)
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .extract();

        this.authSessionCookie = response.cookie(AUTHORIZATION_SESSION_KEY);
        return self();
    }

    public SELF user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
            "restrecord/tpp-ui-input/params/anton-brueckner-account-all-accounts-consent.json"
        );

        return self();
    }

    public SELF user_denied_consent() {
        ExtractableResponse<Response> response = RestAssured
                .given()
                    .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                    .queryParam(REDIRECT_CODE_QUERY, redirectCode)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("{}")
                .when()
                    .post(DENY_CONSENT_AUTH_ENDPOINT, serviceSessionId)
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                .extract();

        assertThat(response.header(LOCATION)).isEqualTo(FINTECH_REDIR_NOK);
        return self();
    }

    public SELF user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp() {
        ExtractableResponse<Response> response = withDefaultHeaders(ANTON_BRUECKNER)
                .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                .queryParam(REDIRECT_CODE_QUERY, redirectCode)
            .when()
                .get(GET_CONSENT_AUTH_STATE, serviceSessionId)
            .then()
                .statusCode(HttpStatus.OK.value())
                .extract();

        this.redirectUriToGetUserParams = response.header(LOCATION);
        updateServiceSessionId(response);
        updateRedirectCode(response);
        return self();
    }

    public SELF user_anton_brueckner_provided_initial_parameters_to_list_transactions_with_single_account_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
            "restrecord/tpp-ui-input/params/anton-brueckner-transactions-single-account-consent.json"
        );

        return self();
    }

    public SELF user_anton_brueckner_provided_initial_parameters_to_list_transactions_with_all_accounts_psd2_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                "restrecord/tpp-ui-input/params/anton-brueckner-transactions-all-accounts-psd2-consent.json"
        );

        return self();
    }

    public SELF user_max_musterman_provided_initial_parameters_to_list_accounts_all_accounts_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                "restrecord/tpp-ui-input/params/max-musterman-account-all-accounts-consent.json"
        );
        return self();
    }

    public SELF user_max_musterman_provided_initial_parameters_to_list_transactions_with_single_account_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
            "restrecord/tpp-ui-input/params/max-musterman-transactions-single-account-consent.json"
        );
        return self();
    }

    public SELF user_max_musterman_provided_initial_parameters_to_list_transactions_with_all_accounts_psd2_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                "restrecord/tpp-ui-input/params/max-musterman-transactions-all-accounts-psd2-consent.json"
        );
        return self();
    }

    public SELF user_max_musterman_provided_initial_parameters_to_list_transactions_but_without_psu_id_with_single_accounts_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                "restrecord/tpp-ui-input/params/unknown-user-transactions-single-account-consent.json"
        );
        return self();
    }

    public SELF user_max_musterman_provided_psu_id_parameter_to_list_transactions_with_single_account_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                "restrecord/tpp-ui-input/params/max-musterman-in-extras.json"
        );
        return self();
    }

    public SELF user_max_musterman_provided_correct_password_after_wrong_to_embedded_authorization() {
        assertThat(this.redirectUriToGetUserParams).contains("ais").contains("authenticate").contains("wrong=true");
        max_musterman_provides_password();
        updateAvailableScas();
        return self();
    }

    public SELF user_max_musterman_provided_password_to_embedded_authorization() {
        assertThat(this.redirectUriToGetUserParams).contains("ais").contains("authenticate").doesNotContain("wrong=true");
        max_musterman_provides_password();
        updateAvailableScas();
        return self();
    }

    public SELF user_max_musterman_provided_wrong_password_to_embedded_authorization_and_stays_on_password_page() {
        ExtractableResponse<Response> response = provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-wrong-password.json"),
                HttpStatus.ACCEPTED
        );

        assertThat(response.header(LOCATION)).contains("ais").contains("authenticate").contains("wrong=true");
        return self();
    }

    public SELF user_max_musterman_selected_sca_challenge_type_email1_to_embedded_authorization() {
        provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
            selectedScaBody("EMAIL:max.musterman@mail.de"),
            HttpStatus.ACCEPTED
        );
        return self();
    }

    public SELF user_max_musterman_selected_sca_challenge_type_email2_to_embedded_authorization() {
        provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
            selectedScaBody("EMAIL:max.musterman2@mail.de"),
            HttpStatus.ACCEPTED
        );
        return self();
    }

    public SELF user_max_musterman_provided_correct_sca_challenge_result_after_wrong_to_embedded_authorization_and_sees_redirect_to_fintech_ok() {
        assertThat(this.redirectUriToGetUserParams).contains("ais").contains("sca-result").contains("wrong=true");
        ExtractableResponse<Response> response = max_musterman_provides_sca_challenge_result();
        assertThat(response.header(LOCATION)).contains("ais").contains("consent-result");
        return self();
    }

    public SELF user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok() {
        assertThat(this.redirectUriToGetUserParams).contains("ais").contains("sca-result").doesNotContain("wrong=true");
        ExtractableResponse<Response> response = max_musterman_provides_sca_challenge_result();
        assertThat(response.header(LOCATION)).contains("ais").contains("consent-result");
        return self();
    }

    public SELF user_max_musterman_provided_wrong_sca_challenge_result_to_embedded_authorization_and_stays_on_sca_page() {
        ExtractableResponse<Response> response = provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-wrong-sca-challenge-result.json"),
                HttpStatus.ACCEPTED
        );

        assertThat(response.header(LOCATION)).contains("ais").contains("sca-result").contains("wrong=true");
        return self();
    }

    private void startInitialInternalConsentAuthorization(String uriPath, String resource) {
        ExtractableResponse<Response> response =
                startInitialInternalConsentAuthorization(uriPath, resource, HttpStatus.ACCEPTED);
        updateServiceSessionId(response);
        updateRedirectCode(response);
    }

    private ExtractableResponse<Response> max_musterman_provides_sca_challenge_result() {
        return provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-sca-challenge-result.json"),
                HttpStatus.ACCEPTED
        );
    }

    private void max_musterman_provides_password() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                "restrecord/tpp-ui-input/params/max-musterman-password.json"
        );
    }


    private ExtractableResponse<Response> startInitialInternalConsentAuthorization(String uriPath, String resource, HttpStatus status) {
        return provideParametersToBankingProtocolWithBody(uriPath, readResource(resource), status);
    }

    private ExtractableResponse<Response> provideParametersToBankingProtocolWithBody(String uriPath, String body, HttpStatus status) {
        ExtractableResponse<Response> response = RestAssured
                .given()
                    .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                    .queryParam(REDIRECT_CODE_QUERY, redirectCode)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                .when()
                    .post(uriPath, serviceSessionId)
                .then()
                    .statusCode(status.value())
                .extract();

        this.responseContent = response.body().asString();
        this.redirectUriToGetUserParams = response.header(LOCATION);
        updateRedirectCode(response);
        return response;
    }

    protected void updateNextConsentAuthorizationUrl(ExtractableResponse<Response> response) {
        this.redirectUriToGetUserParams = response.header(LOCATION);
    }

    protected void updateServiceSessionId(ExtractableResponse<Response> response) {
        this.serviceSessionId = response.header(SERVICE_SESSION_ID);
    }

    protected void updateRedirectCode(ExtractableResponse<Response> response) {
        this.redirectCode = response.header(REDIRECT_CODE);
    }

    @SneakyThrows
    private void updateAvailableScas() {
        ExtractableResponse<Response> response = RestAssured
                .given()
                    .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                    .queryParam(REDIRECT_CODE_QUERY, redirectCode)
                .when()
                    .get(GET_CONSENT_AUTH_STATE, serviceSessionId)
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract();

        InlineResponse200 parsedValue = new ObjectMapper()
            .readValue(response.body().asString(), InlineResponse200.class);

        this.availableScas = parsedValue.getConsentAuth().getScaMethods();
        updateRedirectCode(response);
    }

    private String selectedScaBody(String scaName) {
        return String.format(
                "{\"scaAuthenticationData\":{\"SCA_CHALLENGE_ID\":\"%s\"}}",
                this.availableScas.stream().filter(it -> it.getMethodValue().equals(scaName)).findFirst().get().getId()
        );
    }
}

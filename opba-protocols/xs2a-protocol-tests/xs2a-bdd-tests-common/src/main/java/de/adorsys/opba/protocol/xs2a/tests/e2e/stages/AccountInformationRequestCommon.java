package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.google.common.collect.ImmutableMap;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.consentapi.model.generated.AuthViolation;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.ResourceUtil.readResource;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult.ONLINE;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AIS_ACCOUNTS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AIS_LOGIN_USER_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AIS_TRANSACTIONS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AIS_TRANSACTIONS_WITHOUT_RESOURCE_ID_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.ANTON_BRUECKNER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AUTHORIZE_CONSENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.DENY_CONSENT_AUTH_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.FINTECH_REDIR_NOK;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.GET_CONSENT_AUTH_STATE;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.LOGIN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.MAX_MUSTERMAN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.SANDBOX_BANK_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withAccountsHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withAccountsHeadersMissingIpAddress;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withDefaultHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withTransactionsHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;

@Slf4j
@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class AccountInformationRequestCommon<SELF extends AccountInformationRequestCommon<SELF>> extends RequestCommon<SELF> {
    private static final String TPP_SERVER_USERNAME_PLACEHOLDER = "%user%";
    private static final String TPP_SERVER_IBAN_PLACEHOLDER = "%iban%";

    @ExpectedScenarioState
    protected String iban;

    public SELF fintech_calls_list_accounts_for_anton_brueckner() {
        return fintech_calls_list_accounts_for_anton_brueckner(SANDBOX_BANK_ID);
    }

    // Note that anton.brueckner is typically used for REDIRECT (real REDIRECT that is returned by bank, and not REDIRECT approach in table)
    public SELF fintech_calls_list_accounts_for_anton_brueckner(String bankId) {
        return fintech_calls_list_accounts_for_anton_brueckner(bankId, false);
    }


    public SELF fintech_calls_list_accounts_for_anton_brueckner(String bankId, boolean online) {
        ExtractableResponse<Response> response = withAccountsHeaders(ANTON_BRUECKNER, bankId)
                .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                .queryParam(ONLINE, online)
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

    public SELF fintech_calls_list_accounts_for_user(String user) {
        ExtractableResponse<Response> response = withAccountsHeaders(user)
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

    // Note that max.musterman is typically used for EMBEDDED (real EMBEDDED that is returned by bank, and not EMBEDDED approach in table)
    public SELF fintech_calls_list_accounts_for_max_musterman() {
        return fintech_calls_list_accounts_for_max_musterman(SANDBOX_BANK_ID);
    }

    // Note that max.musterman is typically used for EMBEDDED (real EMBEDDED that is returned by bank, and not EMBEDDED approach in table)
    public SELF fintech_calls_list_accounts_for_max_musterman(String bankId) {
        ExtractableResponse<Response> response = withAccountsHeaders(MAX_MUSTERMAN, bankId)
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

    // Note that max.musterman is typically used for EMBEDDED (real EMBEDDED that is returned by bank, and not EMBEDDED approach in table)
    public SELF fintech_calls_list_accounts_for_max_musterman_with_expected_balances(Boolean withBalance) {
        ExtractableResponse<Response> response = withAccountsHeaders(MAX_MUSTERMAN)
                .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                .queryParam("withBalance", withBalance)
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

    public SELF fintech_calls_list_accounts_for_max_musterman_missing_ip_address() {
        ExtractableResponse<Response> response = withAccountsHeadersMissingIpAddress(MAX_MUSTERMAN)
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

    public SELF fintech_calls_list_transactions_for_anton_brueckner() {
        ExtractableResponse<Response> response = withTransactionsHeaders(ANTON_BRUECKNER)
                .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
            .when()
                .get(AIS_TRANSACTIONS_WITHOUT_RESOURCE_ID_ENDPOINT)
            .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .extract();

        updateServiceSessionId(response);
        updateRedirectCode(response);
        updateNextConsentAuthorizationUrl(response);
        return self();
    }

    public SELF fintech_calls_list_transactions_for_anton_brueckner(String resourceId) {
        ExtractableResponse<Response> response = withTransactionsHeaders(ANTON_BRUECKNER)
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

    public SELF fintech_calls_list_transactions_for_user(String user, String resourceId) {
        ExtractableResponse<Response> response = withTransactionsHeaders(user)
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
        return fintech_calls_list_transactions_for_max_musterman(resourceId, SANDBOX_BANK_ID);
    }

    public SELF fintech_calls_list_transactions_for_max_musterman(String resourceId, String bankId) {
        return fintech_calls_list_transactions_for_max_musterman(resourceId, bankId, false);
    }

    public SELF fintech_calls_list_transactions_for_max_musterman(String resourceId, String bankId, boolean online) {
        ExtractableResponse<Response> response = withTransactionsHeaders(MAX_MUSTERMAN, bankId)
                .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                .queryParam(ONLINE, online)
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

        ExtractableResponse<Response> response =  RestAssured
            .given()
                .header(X_REQUEST_ID, UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
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
        startInitialInternalConsentAuthorizationWithCookieValidation(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/anton-brueckner-account-all-accounts-consent.json")
        );

        return self();
    }

    public SELF user_provided_initial_parameters_to_list_accounts_with_all_accounts_consent_with_cookie_validation(String user) {
        startInitialInternalConsentAuthorizationWithCookieValidation(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/new-user-account-all-accounts-consent.json").replace(TPP_SERVER_USERNAME_PLACEHOLDER, user)
        );

        return self();
    }


    public SELF user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent_without_psu_id() {
        startInitialInternalConsentAuthorization(AUTHORIZE_CONSENT_ENDPOINT,
                                                 readResource("restrecord/tpp-ui-input/params/anton-brueckner-account-all-accounts-consent-without-psu-id.json")
        );
        return self();
    }

    public SELF user_denied_consent() {
        ExtractableResponse<Response> response = RestAssured
            .given()
                .header(X_REQUEST_ID, UUID.randomUUID().toString())
                .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
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

    public SELF user_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp(String user) {
        ExtractableResponse<Response> response = withDefaultHeaders(user)
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
                readResource("restrecord/tpp-ui-input/params/anton-brueckner-transactions-single-account-consent.json")
        );

        return self();
    }

    public SELF user_provided_initial_parameters_to_list_transactions_with_single_account_consent(String user) {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/new-user-transactions-single-account-consent.json")
                        .replace(TPP_SERVER_USERNAME_PLACEHOLDER, user)
                        .replace(TPP_SERVER_IBAN_PLACEHOLDER, iban)
        );

        return self();
    }

    public SELF user_anton_brueckner_provided_initial_parameters_to_list_transactions_with_all_accounts_psd2_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/anton-brueckner-transactions-all-accounts-psd2-consent.json")
        );

        return self();
    }

    public SELF user_max_musterman_provided_initial_parameters_to_list_accounts_all_accounts_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-account-all-accounts-consent.json")
        );
        return self();
    }

    public SELF user_provided_initial_parameters_to_list_accounts_all_accounts_consent(String user) {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/new-user-account-all-accounts-consent.json").replace(TPP_SERVER_USERNAME_PLACEHOLDER, user)
        );
        return self();
    }

    public SELF user_max_musterman_provided_initial_parameters_with_ip_address_to_list_accounts_all_accounts_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-account-all-accounts-consent_with_ip_address.json")
        );
        return self();
    }

    public SELF user_max_musterman_provided_initial_parameters_with_psu_ip_port_to_list_accounts_all_accounts_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-account-all-accounts-consent_with_psu_ip_port.json")
        );
        return self();
    }

    public SELF user_max_musterman_provided_initial_parameters_to_list_transactions_with_single_account_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-transactions-single-account-consent.json")
        );
        return self();
    }

    public SELF user_max_musterman_provided_initial_parameters_with_wrong_iban_to_list_transactions_with_single_account_consent() {

        String resource = "restrecord/tpp-ui-input/params/max-musterman-dedicated-account-consent-wrong-iban.json";

        ExtractableResponse<Response> response =
                startInitialInternalConsentAuthorization(AUTHORIZE_CONSENT_ENDPOINT, readResource(resource), HttpStatus.ACCEPTED);

        assertThat(this.redirectUriToGetUserParams).contains("ais").contains("entry-consent-transactions/dedicated-account-access").contains("wrong=true");

        updateServiceSessionId(response);
        updateRedirectCode(response);
        return self();
    }

    public SELF user_max_musterman_provided_initial_parameters_with_correct_iban_to_list_transactions_with_single_account_consent() {

        String resource = "restrecord/tpp-ui-input/params/max-musterman-transactions-single-account-consent.json";

        ExtractableResponse<Response> response =
                startInitialInternalConsentAuthorization(AUTHORIZE_CONSENT_ENDPOINT, readResource(resource), HttpStatus.ACCEPTED);

        assertThat(this.redirectUriToGetUserParams).contains("authenticate").contains("wrong=false");

        updateServiceSessionId(response);
        updateRedirectCode(response);
        return self();
    }

    public SELF user_max_musterman_provided_initial_parameters_to_list_transactions_with_all_accounts_psd2_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-transactions-all-accounts-psd2-consent.json")
        );
        return self();
    }

    public SELF user_max_musterman_provided_initial_parameters_to_list_transactions_but_without_psu_id_with_single_accounts_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/unknown-user-transactions-single-account-consent.json")
        );
        return self();
    }

    public SELF user_max_musterman_provided_psu_id_parameter_to_list_transactions_with_single_account_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-in-extras.json")
        );
        return self();
    }

    public SELF user_max_musterman_provided_correct_password_after_wrong_to_embedded_authorization() {
        assertThat(this.redirectUriToGetUserParams).contains("authenticate").contains("wrong=true");
        max_musterman_provides_password();
        updateAvailableScas();
        return self();
    }

    public SELF user_max_musterman_provided_password_to_embedded_authorization() {
        assertThat(this.redirectUriToGetUserParams).contains("authenticate").doesNotContain("wrong=true");
        max_musterman_provides_password();
        updateAvailableScas();
        return self();
    }

    public SELF user_provided_password_to_embedded_authorization(String password) {
        assertThat(this.redirectUriToGetUserParams).contains("authenticate").doesNotContain("wrong=true");
        user_provides_password(password);
        updateAvailableScas();
        return self();
    }

    public SELF user_max_musterman_provided_wrong_password_to_embedded_authorization_and_stays_on_password_page() {
        ExtractableResponse<Response> response = provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-wrong-password.json"),
                HttpStatus.ACCEPTED
        );

        assertThat(response.header(LOCATION)).contains("authenticate").contains("wrong=true");
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

    public SELF user_max_musterman_selected_sca_challenge_type_photo_otp_to_embedded_authorization() {
        provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
                selectedScaBody("PHOTO_OTP:photo_otp"),
                HttpStatus.ACCEPTED
        );
        return self();
    }

    public SELF ui_can_read_image_data_from_obg(String user) {
        ExtractableResponse<Response> response = withDefaultHeaders(user)
                .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                .queryParam(REDIRECT_CODE_QUERY, redirectCode)
            .when()
                .get(GET_CONSENT_AUTH_STATE, serviceSessionId)
            .then()
                .statusCode(HttpStatus.OK.value())
                .extract();

        assertThatResponseContainsCorrectChallengeData(response, "restrecord/tpp-ui-input/params/max-musterman-embedded-consent-challenge-data.json");
        updateServiceSessionId(response);
        updateRedirectCode(response);
        return self();
    }

    public SELF user_selected_sca_challenge_type_email1_to_embedded_authorization() {
        provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
                selectedScaBody("EMAIL:test_static@example.com"),
                HttpStatus.ACCEPTED
        );
        return self();
    }

    public SELF user_max_musterman_provided_correct_sca_challenge_result_after_wrong_to_embedded_authorization_and_sees_redirect_to_fintech_ok() {
        assertThat(this.redirectUriToGetUserParams).contains("sca-result").contains("wrong=true");
        ExtractableResponse<Response> response = max_musterman_provides_sca_challenge_result();
        assertThat(response.header(LOCATION)).contains("ais").contains("consent-result");
        return self();
    }

    public SELF user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok() {
        assertThat(this.redirectUriToGetUserParams).contains("sca-result").doesNotContain("wrong=true");
        ExtractableResponse<Response> response = max_musterman_provides_sca_challenge_result();
        assertThat(response.header(LOCATION)).contains("ais").contains("consent-result");
        return self();
    }

    public SELF user_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok() {
        assertThat(this.redirectUriToGetUserParams).contains("sca-result").doesNotContain("wrong=true");
        ExtractableResponse<Response> response = user_provides_sca_challenge_result();
        assertThat(response.header(LOCATION)).contains("ais").contains("consent-result");
        return self();
    }

    public SELF user_max_musterman_provided_wrong_sca_challenge_result_to_embedded_authorization_and_stays_on_sca_page() {
        ExtractableResponse<Response> response = provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-wrong-sca-challenge-result.json"),
                HttpStatus.ACCEPTED
        );

        assertThat(response.header(LOCATION)).contains("sca-result").contains("wrong=true");
        return self();
    }

    @SneakyThrows
    public SELF fintech_calls_get_consent_auth_state_to_read_violations_with_missing_ip_address() {
        readViolations();

        AuthViolation authViolationIpAddress = new AuthViolation()
                                                       .type("STRING")
                                                       .scope("GENERAL")
                                                       .code("PSU_IP_ADDRESS")
                                                       .captionMessage("{no.ctx.psuIpAddress}");
        assertThat(this.violations).contains(authViolationIpAddress);

        return self();
    }

    @SneakyThrows
    public SELF fintech_calls_get_consent_auth_state_to_read_violations_without_missing_ip_address() {
        readViolations();

        AuthViolation authViolationIpAddress = new AuthViolation()
                                                       .type("STRING")
                                                       .scope("GENERAL")
                                                       .code("PSU_IP_ADDRESS")
                                                       .captionMessage("{no.ctx.psuIpAddress}");
        assertThat(this.violations).doesNotContain(authViolationIpAddress);

        return self();
    }

    @SneakyThrows
    public SELF fintech_calls_get_consent_auth_state_to_read_violations_about_missing_psu_ip_port() {
        readViolations();

        AuthViolation authViolationIpPort = new AuthViolation()
                                                       .type("STRING")
                                                       .scope("GENERAL")
                                                       .code("PSU_IP_PORT")
                                                       .captionMessage("{no.ctx.psuIpPort}");
        assertThat(this.violations).contains(authViolationIpPort);

        return self();
    }

    @SneakyThrows
    public SELF fintech_calls_get_consent_auth_state_to_read_violations_without_missing_psu_ip_port() {
        readViolations();

        AuthViolation authViolationIpPort = new AuthViolation()
                                                    .type("STRING")
                                                    .scope("GENERAL")
                                                    .code("PSU_IP_PORT")
                                                    .captionMessage("{no.ctx.psuIpPort}");
        assertThat(this.violations).doesNotContain(authViolationIpPort);

        return self();
    }

    public SELF user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent_without_cookie_unauthorized() {
        RestAssured
                .given()
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                    .queryParam(REDIRECT_CODE_QUERY, redirectCode)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(readResource("restrecord/tpp-ui-input/params/anton-brueckner-account-all-accounts-consent.json"))
                .when()
                    .post(AUTHORIZE_CONSENT_ENDPOINT, serviceSessionId)
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .extract();

        return self();
    }

    public SELF user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp_without_cookie_unauthorized() {
                withDefaultHeaders(ANTON_BRUECKNER)
                    .queryParam(REDIRECT_CODE_QUERY, redirectCode)
                .when()
                    .get(GET_CONSENT_AUTH_STATE, serviceSessionId)
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .extract();

        return self();
    }

    protected ExtractableResponse<Response> startInitialInternalConsentAuthorization(String uriPath, String resourceData) {
        ExtractableResponse<Response> response =
                startInitialInternalConsentAuthorization(uriPath, resourceData, HttpStatus.ACCEPTED);
        updateServiceSessionId(response);
        updateRedirectCode(response);

        return response;
    }

    protected ExtractableResponse<Response> provideParametersToBankingProtocolWithBody(String uriPath, String body, HttpStatus status) {
        return provideParametersToBankingProtocolWithBody(uriPath, body, status, serviceSessionId);
    }

    protected ExtractableResponse<Response> provideGetConsentAuthStateRequest() {
        return provideGetConsentAuthStateRequest(serviceSessionId);
    }
}

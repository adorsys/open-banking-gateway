package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.ResourceUtil.readResource;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.AIS_ACCOUNTS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.AIS_TRANSACTIONS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.ANTON_BRUECKNER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.AUTHORIZE_CONSENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.MAX_MUSTERMAN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.withDefaultHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.REDIRECT_CODE;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;
import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;

@Slf4j
@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class AccountInformationRequestCommon<SELF extends AccountInformationRequestCommon<SELF>> extends Stage<SELF> {

    public static final String REDIRECT_CODE_QUERY = "redirectCode";

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
    private String responseContent;

    @ScenarioState
    private Map<String, String> availableScas;

    @LocalServerPort
    private int serverPort;

    @BeforeStage
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost:" + serverPort;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        config = config().redirect(redirectConfig().followRedirects(false));
    }

    public SELF open_banking_list_accounts_called_for_anton_brueckner() {
        ExtractableResponse<Response> response = withDefaultHeaders(ANTON_BRUECKNER)
                .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                .when()
                    .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .extract();
        updateExecutionId(response);
        updateRedirectCode(response);
        updateNextConsentAuthorizationUrl(response);
        return self();
    }

    public SELF open_banking_list_accounts_called_for_max_musterman() {
        ExtractableResponse<Response> response = withDefaultHeaders(MAX_MUSTERMAN)
                .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                .when()
                    .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .extract();

        updateExecutionId(response);
        updateRedirectCode(response);
        updateNextConsentAuthorizationUrl(response);
        return self();
    }

    public SELF open_banking_list_transactions_called_for_anton_brueckner(String resourceId) {
        ExtractableResponse<Response> response = withDefaultHeaders(ANTON_BRUECKNER)
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                .when()
                    .get(AIS_TRANSACTIONS_ENDPOINT, resourceId)
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                .extract();

        updateExecutionId(response);
        updateRedirectCode(response);
        updateNextConsentAuthorizationUrl(response);
        return self();
    }

    public SELF open_banking_list_transactions_called_for_max_musterman() {
        return open_banking_list_transactions_called_for_max_musterman("oN7KTVuJSVotMvPPPavhVo");
    }

    public SELF open_banking_list_transactions_called_for_max_musterman(String resourceId) {
        ExtractableResponse<Response> response = withDefaultHeaders(MAX_MUSTERMAN)
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                .when()
                    .get(AIS_TRANSACTIONS_ENDPOINT, resourceId)
                .then()
                    .statusCode(HttpStatus.MOVED_PERMANENTLY.value())
                    .extract();

        updateExecutionId(response);
        updateRedirectCode(response);
        updateNextConsentAuthorizationUrl(response);
        return self();
    }

    public SELF open_banking_user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
            "restrecord/tpp-ui-input/params/anton-brueckner-account-all-accounts-consent.json"
        );

        return self();
    }

    public SELF open_banking_user_anton_brueckner_provided_initial_parameters_to_list_transactions() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
            "restrecord/tpp-ui-input/params/anton-brueckner-transactions.txt"
        );

        return self();
    }

    public SELF open_banking_user_max_musterman_provided_initial_parameters_to_list_accounts() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                "restrecord/tpp-ui-input/params/max-musterman-account-all-accounts-consent.json"
        );
        return self();
    }

    public SELF open_banking_user_max_musterman_provided_initial_parameters_to_list_transactions() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
            "restrecord/tpp-ui-input/params/max-musterman-transactions.txt"
        );
        return self();
    }

    public SELF open_banking_user_max_musterman_provided_password() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                "restrecord/tpp-ui-input/params/max-musterman-password.json"
        );
        updateAvailableScas();
        return self();
    }

    public SELF open_banking_user_max_musterman_selected_sca_challenge_type_email1() {
        provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
            selectedScaBody("EMAIL:max.musterman@mail.de"),
            HttpStatus.SEE_OTHER
        );
        return self();
    }

    public SELF open_banking_user_max_musterman_selected_sca_challenge_type_email2() {
        provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
            selectedScaBody("EMAIL:max.musterman2@mail.de"),
            HttpStatus.SEE_OTHER
        );
        return self();
    }

    public SELF open_banking_user_max_musterman_provided_sca_challenge_result_and_redirect_to_fintech_ok() {
        ExtractableResponse<Response> response = provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-sca-challenge-result.json"),
                HttpStatus.SEE_OTHER
        );

        assertThat(response.header(LOCATION)).contains("localhost").contains("/ok");
        return self();
    }

    private void startInitialInternalConsentAuthorization(String uriPath, String resource) {
        ExtractableResponse<Response> response =
                startInitialInternalConsentAuthorization(uriPath, resource, HttpStatus.SEE_OTHER);
        updateExecutionId(response);
        updateRedirectCode(response);
    }

    private ExtractableResponse<Response> startInitialInternalConsentAuthorization(String uriPath, String resource, HttpStatus status) {
        return provideParametersToBankingProtocolWithBody(uriPath, readResource(resource), status);
    }

    private ExtractableResponse<Response> provideParametersToBankingProtocolWithBody(String uriPath, String body, HttpStatus status) {
        ExtractableResponse<Response> response = RestAssured
                .given()
                    .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
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

    private void updateNextConsentAuthorizationUrl(ExtractableResponse<Response> response) {
        this.redirectUriToGetUserParams = response.header(LOCATION);
    }

    private void updateExecutionId(ExtractableResponse<Response> response) {
        serviceSessionId = response.header(SERVICE_SESSION_ID);
    }

    private void updateRedirectCode(ExtractableResponse<Response> response) {
        redirectCode = response.header(REDIRECT_CODE);
    }

    @SneakyThrows
    private void updateAvailableScas() {
        String value = URLDecoder.decode(
            this.redirectUriToGetUserParams.split("\\?")[1].split("=")[1],
            StandardCharsets.UTF_8.name()
        );
        List<AuthDto> parsedValue = new ObjectMapper()
            .readValue(value, new TypeReference<List<AuthDto>>() { });

        this.availableScas = parsedValue.stream().collect(
            Collectors.toMap(AuthDto::getValue, AuthDto::getKey)
        );
    }

    private String selectedScaBody(String scaName) {
        return String.format("{\"scaAuthenticationData\":{\"scaMethodId\":\"%s\"}}", this.availableScas.get(scaName));
    }

    @Data
    private static class AuthDto {

        private String key;
        private String value;
    }
}

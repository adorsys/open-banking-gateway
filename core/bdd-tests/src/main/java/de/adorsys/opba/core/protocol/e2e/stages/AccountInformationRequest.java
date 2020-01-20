package de.adorsys.opba.core.protocol.e2e.stages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Durations;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static de.adorsys.opba.core.protocol.e2e.ResourceUtil.readResource;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_REDIRECT_URI;
import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.awaitility.Awaitility.await;

@Slf4j
@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class AccountInformationRequest extends Stage<AccountInformationRequest> {

    public static final String PARAMETERS_PROVIDE_MORE = "/v1/parameters/provide-more/";

    @ProvidedScenarioState
    private String redirectUriToGetUserParams;

    @ProvidedScenarioState
    private String execId;

    @ProvidedScenarioState
    @SuppressWarnings("PMD.UnusedPrivateField") // used by AccountListResult!
    private String redirectOkUri;

    @ProvidedScenarioState
    @SuppressWarnings("PMD.UnusedPrivateField") // used by AccountListResult!
    private String responseContent;

    @ScenarioState
    private Map<String, String> availableScas;

    @ExpectedScenarioState
    private WireMockServer wireMock;

    @LocalServerPort
    private int serverPort;

    @BeforeStage
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost:" + serverPort;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        config = config().redirect(redirectConfig().followRedirects(false));
    }

    public AccountInformationRequest open_banking_list_accounts_called() {
        this.redirectUriToGetUserParams = RestAssured
                .when()
                    .get("/v1/accounts")
                .then()
                    .statusCode(HttpStatus.MOVED_PERMANENTLY.value())
                    .extract()
                    .header(HttpHeaders.LOCATION);
        updateExecutionId();
        return self();
    }

    public AccountInformationRequest open_banking_list_transactions_called_for_anton_brueckner() {
        this.redirectUriToGetUserParams = RestAssured
                .when()
                    .get("/v1/transactions/cmD4EYZeTkkhxRuIV1diKA")
                .then()
                    .statusCode(HttpStatus.MOVED_PERMANENTLY.value())
                .extract()
                    .header(HttpHeaders.LOCATION);
        updateExecutionId();
        return self();
    }

    public AccountInformationRequest open_banking_list_transactions_called_for_max_musterman() {
        return open_banking_list_transactions_called_for_max_musterman("oN7KTVuJSVotMvPPPavhVo");
    }

    public AccountInformationRequest open_banking_list_transactions_called_for_max_musterman(String resourceId) {
        this.redirectUriToGetUserParams = RestAssured
                .when()
                .get("/v1/transactions/" + resourceId)
                .then()
                .statusCode(HttpStatus.MOVED_PERMANENTLY.value())
                .extract()
                .header(HttpHeaders.LOCATION);
        updateExecutionId();
        return self();
    }

    public AccountInformationRequest open_banking_user_anton_brueckner_provided_initial_parameters_to_list_accounts() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
            "restrecord/tpp-ui-input/params/anton-brueckner-account.txt"
        );

        LoggedRequest consentInitiateRequest = await().atMost(Durations.TEN_SECONDS)
                .until(() ->
                        wireMock.findAll(postRequestedFor(urlMatching("/v1/consents.*"))), it -> !it.isEmpty()
                ).get(0);
        redirectOkUri = consentInitiateRequest.getHeader(TPP_REDIRECT_URI);
        return self();
    }

    public AccountInformationRequest open_banking_user_anton_brueckner_provided_initial_parameters_to_list_transactions() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
            "restrecord/tpp-ui-input/params/anton-brueckner-transactions.txt"
        );

        LoggedRequest consentInitiateRequest = await().atMost(Durations.TEN_SECONDS)
                .until(() ->
                        wireMock.findAll(postRequestedFor(urlMatching("/v1/consents.*"))), it -> !it.isEmpty()
                ).get(0);
        redirectOkUri = consentInitiateRequest.getHeader(TPP_REDIRECT_URI);
        return self();
    }

    public AccountInformationRequest open_banking_user_max_musterman_provided_initial_parameters_to_list_accounts() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
            "restrecord/tpp-ui-input/params/max-musterman-account.txt"
        );
        return self();
    }

    public AccountInformationRequest open_banking_user_max_musterman_provided_initial_parameters_to_list_transactions() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
            "restrecord/tpp-ui-input/params/max-musterman-transactions.txt"
        );
        return self();
    }

    public AccountInformationRequest open_banking_user_max_musterman_provided_password() {
        provideParametersToBankingProtocol(
                "/v1/parameters/provide-psu-password/",
            "restrecord/tpp-ui-input/params/max-musterman-password.txt"
        );
        updateAvailableScas();
        return self();
    }

    public AccountInformationRequest open_banking_user_max_musterman_selected_sca_challenge_type_email1() {
        provideParametersToBankingProtocolWithBody(
                "/v1/parameters/select-sca-method/",
            selectedScaBody("EMAIL:max.musterman@mail.de"),
            HttpStatus.MOVED_PERMANENTLY
        );
        return self();
    }

    public AccountInformationRequest open_banking_user_max_musterman_selected_sca_challenge_type_email2() {
        provideParametersToBankingProtocolWithBody(
                "/v1/parameters/select-sca-method/",
            selectedScaBody("EMAIL:max.musterman2@mail.de"),
            HttpStatus.MOVED_PERMANENTLY
        );
        return self();
    }

    public AccountInformationRequest open_banking_user_max_musterman_provided_sca_challenge_result_and_no_redirect() {
        provideParametersToBankingProtocol(
                "/v1/parameters/report-sca-result/",
            "restrecord/tpp-ui-input/params/max-musterman-sca-challenge-result.txt",
                HttpStatus.OK
        );

        return self();
    }

    private void provideParametersToBankingProtocol(String uriPath, String resource) {
        provideParametersToBankingProtocol(uriPath, resource, HttpStatus.MOVED_PERMANENTLY);
        updateExecutionId();
    }

    private void provideParametersToBankingProtocol(String uriPath, String resource, HttpStatus status) {
        provideParametersToBankingProtocolWithBody(uriPath, readResource(resource), status);
    }

    private void provideParametersToBankingProtocolWithBody(String uriPath, String body, HttpStatus status) {
        ExtractableResponse<Response> response = RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(body)
                .when()
                    .post(uriPath + execId)
                .then()
                    .statusCode(status.value())
                .extract();

        this.responseContent = response.body().asString();
        this.redirectUriToGetUserParams = response.header(HttpHeaders.LOCATION);
    }

    private void updateExecutionId() {
        execId = Iterables.getLast(
                Splitter.on("/").split(Splitter.on("?").split(redirectUriToGetUserParams).iterator().next())
        );
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
        return "scaMethodId=" + this.availableScas.get(scaName);
    }

    @Data
    private static class AuthDto {

        private String key;
        private String value;
    }
}

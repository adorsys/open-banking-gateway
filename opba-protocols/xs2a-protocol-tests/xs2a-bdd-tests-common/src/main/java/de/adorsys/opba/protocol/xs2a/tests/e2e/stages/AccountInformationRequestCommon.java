package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.AUTHORIZATION;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.BANK_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_REDIRECT_URL_NOK;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_REDIRECT_URL_OK;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_USER_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.SERVICE_SESSION_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.SERVICE_SESSION_PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.ResourceUtil.readResourceSkipLastEol;
import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;

@Slf4j
@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class AccountInformationRequestCommon<SELF extends AccountInformationRequestCommon<SELF>> extends Stage<SELF> {

    private static final String DEFAULT_AUTHORIZATION = "MY-SUPER-FINTECH-ID";
    private static final String SANDBOX_BANK_ID = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
    private static final String FINTECH_REDIR_OK = "http://localhost:5500/fintech-callback/ok";
    private static final String FINTECH_REDIR_NOK = "http://localhost:5500/fintech-callback/nok";
    private static final String SESSION_PASSWORD = "qwerty";
    private static final String ANTON_BRUECKNER = "anton.brueckner";
    private static final String MAX_MUSTERMAN = "max.musterman";

    public static final String PARAMETERS_PROVIDE_MORE = "/v1/parameters/provide-more/";

    @ProvidedScenarioState
    protected String redirectUriToGetUserParams;

    @ProvidedScenarioState
    protected String execId;

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
        this.redirectUriToGetUserParams = withInitialHeaders(ANTON_BRUECKNER)
                .when()
                    .get("/v1/banking/ais/accounts")
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .extract()
                    .header(HttpHeaders.LOCATION);
        updateExecutionId();
        return self();
    }

    public SELF open_banking_list_accounts_called_for_max_musterman() {
        this.redirectUriToGetUserParams = withInitialHeaders(MAX_MUSTERMAN)
                .when()
                    .get("/v1/banking/ais/accounts")
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .extract()
                .header(HttpHeaders.LOCATION);
        updateExecutionId();
        return self();
    }

    public SELF open_banking_list_transactions_called_for_anton_brueckner(String resourceId) {
        this.redirectUriToGetUserParams = withInitialHeaders(ANTON_BRUECKNER)
                .when()
                    .get("/v1/transactions/" + resourceId)
                .then()
                    .statusCode(HttpStatus.MOVED_PERMANENTLY.value())
                .extract()
                    .header(HttpHeaders.LOCATION);
        updateExecutionId();
        return self();
    }

    public SELF open_banking_list_transactions_called_for_max_musterman() {
        return open_banking_list_transactions_called_for_max_musterman("oN7KTVuJSVotMvPPPavhVo");
    }

    public SELF open_banking_list_transactions_called_for_max_musterman(String resourceId) {
        this.redirectUriToGetUserParams = withInitialHeaders(MAX_MUSTERMAN)
                .when()
                    .get("/v1/transactions/" + resourceId)
                .then()
                    .statusCode(HttpStatus.MOVED_PERMANENTLY.value())
                    .extract()
                .header(HttpHeaders.LOCATION);
        updateExecutionId();
        return self();
    }

    public SELF open_banking_user_anton_brueckner_provided_initial_parameters_to_list_accounts() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
            "restrecord/tpp-ui-input/params/anton-brueckner-account.txt"
        );

        return self();
    }

    public SELF open_banking_user_anton_brueckner_provided_initial_parameters_to_list_transactions() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
            "restrecord/tpp-ui-input/params/anton-brueckner-transactions.txt"
        );

        return self();
    }

    public SELF open_banking_user_max_musterman_provided_initial_parameters_to_list_accounts() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
            "restrecord/tpp-ui-input/params/max-musterman-account.txt"
        );
        return self();
    }

    public SELF open_banking_user_max_musterman_provided_initial_parameters_to_list_transactions() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
            "restrecord/tpp-ui-input/params/max-musterman-transactions.txt"
        );
        return self();
    }

    public SELF open_banking_user_max_musterman_provided_password() {
        provideParametersToBankingProtocol(
                "/v1/parameters/provide-psu-password/",
            "restrecord/tpp-ui-input/params/max-musterman-password.txt"
        );
        updateAvailableScas();
        return self();
    }

    public SELF open_banking_user_max_musterman_selected_sca_challenge_type_email1() {
        provideParametersToBankingProtocolWithBody(
                "/v1/parameters/select-sca-method/",
            selectedScaBody("EMAIL:max.musterman@mail.de"),
            HttpStatus.MOVED_PERMANENTLY
        );
        return self();
    }

    public SELF open_banking_user_max_musterman_selected_sca_challenge_type_email2() {
        provideParametersToBankingProtocolWithBody(
                "/v1/parameters/select-sca-method/",
            selectedScaBody("EMAIL:max.musterman2@mail.de"),
            HttpStatus.MOVED_PERMANENTLY
        );
        return self();
    }

    public SELF open_banking_user_max_musterman_provided_sca_challenge_result_and_no_redirect() {
        provideParametersToBankingProtocol(
                "/v1/parameters/report-sca-result/",
            "restrecord/tpp-ui-input/params/max-musterman-sca-challenge-result.txt",
                HttpStatus.OK
        );

        return self();
    }

    private RequestSpecification withInitialHeaders(String fintechUserId) {
        return RestAssured
                .given()
                .header(AUTHORIZATION, DEFAULT_AUTHORIZATION)
                .header(BANK_ID, SANDBOX_BANK_ID)
                .header(FINTECH_REDIRECT_URL_OK, FINTECH_REDIR_OK)
                .header(FINTECH_REDIRECT_URL_NOK, FINTECH_REDIR_NOK)
                .header(FINTECH_USER_ID, fintechUserId)
                .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                .header(SERVICE_SESSION_PASSWORD, SESSION_PASSWORD)
                .header(X_REQUEST_ID, UUID.randomUUID().toString());
    }

    private void provideParametersToBankingProtocol(String uriPath, String resource) {
        provideParametersToBankingProtocol(uriPath, resource, HttpStatus.MOVED_PERMANENTLY);
        updateExecutionId();
    }

    private void provideParametersToBankingProtocol(String uriPath, String resource, HttpStatus status) {
        provideParametersToBankingProtocolWithBody(uriPath, readResourceSkipLastEol(resource), status);
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

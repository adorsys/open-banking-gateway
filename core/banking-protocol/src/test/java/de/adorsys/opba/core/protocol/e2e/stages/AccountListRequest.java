package de.adorsys.opba.core.protocol.e2e.stages;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static de.adorsys.opba.core.protocol.e2e.ResourceUtil.readResource;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_REDIRECT_URI;
import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;

@Slf4j
@JGivenStage
public class AccountListRequest extends Stage<AccountListRequest> {

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

    public AccountListRequest open_banking_list_accounts_called() {
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

    public AccountListRequest open_banking_list_transactions_called_for_anton_brueckner() {
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

    public AccountListRequest open_banking_list_transactions_called_for_max_musterman() {
        this.redirectUriToGetUserParams = RestAssured
                .when()
                    .get("/v1/transactions/oN7KTVuJSVotMvPPPavhVo")
                .then()
                    .statusCode(HttpStatus.MOVED_PERMANENTLY.value())
                .extract()
                    .header(HttpHeaders.LOCATION);
        updateExecutionId();
        return self();
    }

    public AccountListRequest open_banking_user_anton_brueckner_provided_initial_parameters_to_list_accounts() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
            "mockedsandbox/restrecord/tpp-ui-input/params/anton-brueckner-account.txt"
        );

        LoggedRequest consentInitiateRequest = wireMock
                .findAll(postRequestedFor(urlMatching("/v1/consents.*"))).get(0);
        redirectOkUri = consentInitiateRequest.getHeader(TPP_REDIRECT_URI);
        return self();
    }

    public AccountListRequest open_banking_user_anton_brueckner_provided_initial_parameters_to_list_transactions() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
            "mockedsandbox/restrecord/tpp-ui-input/params/anton-brueckner-transactions.txt"
        );

        LoggedRequest consentInitiateRequest = wireMock
                .findAll(postRequestedFor(urlMatching("/v1/consents.*"))).get(0);
        redirectOkUri = consentInitiateRequest.getHeader(TPP_REDIRECT_URI);
        return self();
    }

    public AccountListRequest open_banking_user_max_musterman_provided_initial_parameters_to_list_accounts() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
            "mockedsandbox/restrecord/tpp-ui-input/params/max-musterman-account.txt"
        );
        return self();
    }

    public AccountListRequest open_banking_user_max_musterman_provided_initial_parameters_to_list_transactions() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
            "mockedsandbox/restrecord/tpp-ui-input/params/max-musterman-transactions.txt"
        );
        return self();
    }

    public AccountListRequest open_banking_user_max_musterman_provided_password() {
        provideParametersToBankingProtocol(
                "/v1/parameters/provide-psu-password/",
            "mockedsandbox/restrecord/tpp-ui-input/params/max-musterman-password.txt"
        );
        return self();
    }

    public AccountListRequest open_banking_user_max_musterman_selected_sca_challenge_type_email1() {
        provideParametersToBankingProtocol(
                "/v1/parameters/select-sca-method/",
            "mockedsandbox/restrecord/tpp-ui-input/params/max-musterman-selected-sca-email1.txt"
        );
        return self();
    }

    public AccountListRequest open_banking_user_max_musterman_selected_sca_challenge_type_email2() {
        provideParametersToBankingProtocol(
                "/v1/parameters/select-sca-method/",
            "mockedsandbox/restrecord/tpp-ui-input/params/max-musterman-selected-sca-email2.txt"
        );
        return self();
    }

    public AccountListRequest open_banking_user_max_musterman_provided_sca_challenge_result_and_no_redirect() {
        provideParametersToBankingProtocol(
                "/v1/parameters/report-sca-result/",
            "mockedsandbox/restrecord/tpp-ui-input/params/max-musterman-sca-challenge-result.txt",
                HttpStatus.OK
        );

        return self();
    }

    private void provideParametersToBankingProtocol(String uriPath, String resource) {
        provideParametersToBankingProtocol(uriPath, resource, HttpStatus.MOVED_PERMANENTLY);
        updateExecutionId();
    }

    private void provideParametersToBankingProtocol(String uriPath, String resource, HttpStatus status) {
        ExtractableResponse<Response> response = RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(readResource(resource))
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
}

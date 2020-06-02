package de.adorsys.fintech.tests.e2e.steps;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.ACCOUNT;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.BANKSEARCH_LOGIN;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.KEYWORD;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.FINTECH_UI_URI;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.SESSION_COOKIE;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.FINTECH_SERVER_LOGIN;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.ACCOUNT_ENDPOINT;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.X_REQUEST_ID;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.X_XSRF_TOKEN;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.withDefaultHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationRequestCommon.REDIRECT_CODE_QUERY;


@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class UserInformationResult extends AccountInformationResult {

    @Getter
    @ExpectedScenarioState
    private String respContent;

    @ExpectedScenarioState
    protected String xsrfToken;

    @ExpectedScenarioState
    protected String sessionCookie;

    @SneakyThrows
    public UserInformationResult fintech_can_read_anton_brueckner_accounts_and_transactions() {
        ExtractableResponse<Response> response = withDefaultHeaders()
                                                         .given()
                                                             .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                                                             .header(SESSION_COOKIE, UUID.randomUUID().toString())
                                                         .when()
                                                             .get(ACCOUNT )
                                                         .then()
                                                             .statusCode(HttpStatus.OK.value())
                                                             .extract();
        this.respContent = response.body().asString();
        return (UserInformationResult) self();
    }

    public UserInformationResult fintech_can_read_user_accounts_and_transactions() {
        ExtractableResponse<Response> response = withDefaultHeaders()
                                                         .given()
                                                             .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                                                             .header(SESSION_COOKIE, UUID.randomUUID().toString())
                                                         .when()
                                                             .get(ACCOUNT )
                                                         .then()
                                                             .statusCode(HttpStatus.OK.value())
                                                             .extract();
        this.respContent = response.body().asString();
        return (UserInformationResult) self();
    }

    public UserInformationResult fintech_navigates_back_to_login_after_user_logs_out() {
        ExtractableResponse<Response> response = withDefaultHeaders()
                                                         .when()
                                                            .get(BANKSEARCH_LOGIN)
                                                         .then()
                                                            .statusCode(HttpStatus.OK.value())
                                                            .extract();
        this.respContent = response.body().asString();
        return (UserInformationResult) self();
    }

    @SneakyThrows
    public UserInformationResult fintech_get_bank_infos(String username) {
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        ExtractableResponse<Response> response = RestAssured
                                                         .given()
                                                         .header(X_REQUEST_ID, UUID.randomUUID().toString())
                                                         .header(X_XSRF_TOKEN, xsrfToken)
                                                         .queryParam("keyword", KEYWORD)
                                                         .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                         .body(request)
                                                         .when()
                                                             .get(FINTECH_UI_URI + "/search")
                                                         .then()
                                                             .statusCode(HttpStatus.OK.value())
                                                             .extract();
        this.respContent = response.body().asString();
        return (UserInformationResult) self();
    }

    public UserInformationResult login_and_get_cookies(String username) {
        Map<String, String> request = new HashMap<>();
        request.put("password", "1234");
        request.put("username", username);

        ExtractableResponse<Response> response = RestAssured
                                                         .given()
                                                         .header(X_REQUEST_ID, UUID.randomUUID().toString())
                                                         .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                                                         .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                         .body(request)
                                                         .when()
                                                         .post(FINTECH_SERVER_LOGIN)
                                                         .then()
                                                         .statusCode(HttpStatus.OK.value())
                                                         .extract();
        this.respContent = response.body().asString();
        xsrfToken = response.header(X_XSRF_TOKEN);
        sessionCookie = response.cookie("SESSION-COOKIE");
        return (UserInformationResult) self();
    }

    public UserInformationResult fintech_get_user_infos() {
        ExtractableResponse<Response> response = RestAssured
                                                         .given()
                                                         .header(X_REQUEST_ID, UUID.randomUUID().toString())
                                                         .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                                                         .queryParam(REDIRECT_CODE_QUERY, redirectCode)
                                                         .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                         .when()
                                                             .get(ACCOUNT_ENDPOINT)
                                                         .then()
                                                             .statusCode(HttpStatus.OK.value())
                                                             .extract();
        this.respContent = response.body().asString();
        return (UserInformationResult) self();
    }
}

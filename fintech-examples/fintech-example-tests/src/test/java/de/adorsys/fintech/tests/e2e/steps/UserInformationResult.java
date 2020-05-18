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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.*;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationRequestCommon.REDIRECT_CODE_QUERY;


@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class UserInformationResult extends AccountInformationResult {

    @Getter
    @ExpectedScenarioState
    private String respContent;

    private static String xsrfToken;

    @SneakyThrows
    public UserInformationResult fintech_can_read_anton_brueckner_accounts_and_transactions(String accountId, String bankId) {
        ExtractableResponse<Response> response = withDefaultHeaders()
                                                         .given()
                                                             .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                                                             .header(SESSION_COOKIE, UUID.randomUUID().toString())
                                                         .when()
                                                             .get(BANKPROFILE_ENDPOINT + bankId + ACCOUNT + accountId)
                                                         .then()
                                                             .statusCode(HttpStatus.OK.value())
                                                             .extract();
        this.respContent = response.body().asString();
        return (UserInformationResult) self();
    }

    public UserInformationResult fintech_can_read_user_accounts_and_transactions(String accountId, String bankId) {
        ExtractableResponse<Response> response = withDefaultHeaders()
                                                         .given()
                                                             .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                                                             .header(SESSION_COOKIE, UUID.randomUUID().toString())
                                                         .when()
                                                             .get(BANKPROFILE_ENDPOINT + bankId + ACCOUNT + accountId)
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
    public UserInformationResult fintech_get_bank_infos() {
        UserInformationResult resp = login_and_get_cookies();
        Pattern pattern = Pattern.compile("(?<=hashedXsrfToken=)\\d+");
        Matcher matcher = pattern.matcher(resp.respContent);
        xsrfToken = matcher.group();

        ExtractableResponse<Response> response = RestAssured
                                                         .given()
                                                         .header(X_REQUEST_ID, UUID.randomUUID().toString())
                                                         .header(X_XSRF_TOKEN, xsrfToken)
                                                         .cookie("SESSION-COOKIE", resp.respContent)
                                                         .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                         .when()
                                                             .get(BANKSEARCH_ENDPOINT + KEYWORD)
                                                         .then()
                                                             .statusCode(HttpStatus.OK.value())
                                                             .extract();
        this.respContent = response.body().asString();
        xsrfToken = response.header(X_XSRF_TOKEN);
        return (UserInformationResult) self();
    }

    public UserInformationResult login_and_get_cookies() {
        Map<String, String> request = new HashMap<>();
        request.put("password", "1234");
        request.put("username", fintech_login);

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
        this.respContent = response.cookie("SESSION-COOKIE");
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

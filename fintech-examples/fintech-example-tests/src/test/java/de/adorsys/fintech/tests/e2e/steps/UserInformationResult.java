package de.adorsys.fintech.tests.e2e.steps;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.*;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;


@JGivenStage
@SuppressWarnings("checkstyle:MethodName")
// Jgiven prettifies snake-case names not camelCase
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
                                                         .get(ACCOUNT)
                                                         .then()
                                                         .statusCode(HttpStatus.OK.value())
                                                         .extract();
        this.respContent = response.body().asString();
        return (UserInformationResult) self();
    }

    @SneakyThrows
    public UserInformationResult fintech_can_read_users_accounts_and_transactions() {
        ExtractableResponse<Response> response = withDefaultHeaders()
                                                         .given()
                                                         .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                                                         .header(SESSION_COOKIE, UUID.randomUUID().toString())
                                                         .when()
                                                         .get(TRANSACTION)
                                                         .then()
                                                         .statusCode(HttpStatus.OK.value())
                                                         .extract();
        this.respContent = response.body().asString();
        return (UserInformationResult) self();
    }

    public UserInformationResult fintech_can_read_user_accounts() {
        ExtractableResponse<Response> response = withDefaultHeaders()
                                                         .given()
                                                         .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                                                         .header(SESSION_COOKIE, UUID.randomUUID().toString())
                                                         .when()
                                                         .get(ACCOUNT)
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
}

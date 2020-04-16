package de.adorsys.fintech.tests.e2e.steps;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.withDefaultHeaders;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.*;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.withDefaultHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.*;


@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class UserInformationResult extends AccountInformationResult {

    @Getter
    @ExpectedScenarioState
    private String respContent;

    @SneakyThrows
    public UserInformationResult fintech_can_read_anton_brueckner_accounts_and_transactions() {
        ExtractableResponse<Response> response = withDefaultHeaders(ANTON_BRUECKNER)
                                                         .header(X_XSRF_TOKEN, X_XSRF_TOKEN_VALUE)
                                                         .header(SESSION_COOKIE, SESSION_COOKIE_VALUE)
                                                         .when()
                                                         .get(BANKPROFILE_ENDPOINT + BANK_ID_VALUE + ACCOUNT + ANTON_BRUECKNER_ID)
                                                         .then()
                                                         .statusCode(HttpStatus.OK.value())
                                                         .extract();
        this.respContent = response.body().asString();
        return (UserInformationResult) self();
    }

    public UserInformationResult fintech_can_read_max_musterman_accounts_and_transactions() {
        ExtractableResponse<Response> response = withDefaultHeaders(MAX_MUSTERMAN)
                                                         .header(X_XSRF_TOKEN, X_XSRF_TOKEN_VALUE)
                                                         .header(SESSION_COOKIE, SESSION_COOKIE_VALUE)
                                                         .when()
                                                         .get(BANKPROFILE_ENDPOINT + BANK_ID_VALUE + ACCOUNT + MAX_MUSTERMAN_ID)
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

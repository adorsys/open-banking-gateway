package de.adorsys.fintech.tests.e2e.steps;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

import javax.transaction.Transactional;
import java.util.UUID;

import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.BANKSEARCH_ENDPOINT;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.BANK_ID;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.BANK_ID_VALUE;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.FINTECH_LOGIN_ENDPOINT;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.SESSION_COOKIE;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.SESSION_COOKIE_VALUE;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.USERNAME;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.X_REQUEST_ID;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.X_XSRF_TOKEN;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.X_XSRF_TOKEN_VALUE;
import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.withDefaultHeaders;
import static org.hamcrest.Matchers.equalTo;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class UserInformationResult extends Stage<UserInformationResult> {

    //@Autowired
    //private UserRepository users;

    @Getter
    @ExpectedScenarioState
    private String responseContent;

    @ExpectedScenarioState
    private String xsrfToken;

    @ExpectedScenarioState
    private String sessionCookie;

    @SneakyThrows
    @Transactional
    public UserInformationResult fintech_knows_user_who_entered_his_credentials() {
        //assertThat(users.findBySessionCookieValue(sessionCookie)).isNotEmpty();
        return self();
    }

    @SneakyThrows
    public UserInformationResult fintech_log_out_user() {
        //users.deleteBySessionCookieValue(sessionCookie);
        //assertThat(users.findBySessionCookieValue(sessionCookie)).isEqualTo(null);
        return self();
    }

    @SneakyThrows
    public UserInformationResult fintech_can_read_user_data_using_xsrfToken() {
        ExtractableResponse<Response> response = withDefaultHeaders()
                                                         .header(X_XSRF_TOKEN, X_XSRF_TOKEN_VALUE)
                                                         .when().get(FINTECH_LOGIN_ENDPOINT)
                                                         .then().statusCode(HttpStatus.OK.value())
                                                         .body("userProfile.name", equalTo(USERNAME))
                                                         .body("userProfile.lastLogin", equalTo(null))
                                                         .extract();
        this.responseContent = response.body().asString();
        return self();
    }

    //{"bankProfile":{"bankId":null,"bankName":"adorsys xs2a","bic":"ADORSYS",
    // "services":["AUTHORIZATION","LIST_TRANSACTIONS","LIST_ACCOUNTS"]}}
    @SneakyThrows
    public UserInformationResult fintech_can_read_bank_profile_using_xsrfToken(String bankName) {
        ExtractableResponse<Response> response = withDefaultHeaders()
                                                         .header(X_XSRF_TOKEN, X_XSRF_TOKEN_VALUE)
                                                         .header(X_REQUEST_ID, UUID.randomUUID().toString())
                                                         .header(SESSION_COOKIE, SESSION_COOKIE_VALUE)
                                                         .queryParam("keyword", bankName)
                                                         .when().get(BANKSEARCH_ENDPOINT, BANK_ID)
                                                         .then().statusCode(HttpStatus.OK.value())
                                                         .body("bankProfile.bankId", equalTo(BANK_ID_VALUE))
                                                         .body("bankProfile.bankName", equalTo(bankName))
                                                         .body("bankProfile.bic", equalTo("ADORSYS"))
                                                         .body("bankProfile.service[0]", equalTo("AUTHORIZATION"))
                                                         .body("bankProfile.service[1]", equalTo("LIST_TRANSACTIONS"))
                                                         .body("bankProfile.service[1]", equalTo("LIST_ACCOUNTS"))
                                                         .extract();
        this.responseContent = response.body().asString();
        return self();
    }
}

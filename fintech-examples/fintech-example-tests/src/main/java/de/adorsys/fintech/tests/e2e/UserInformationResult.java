package de.adorsys.fintech.tests.e2e;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import io.restassured.http.Cookie;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;

import javax.transaction.Transactional;

import static de.adorsys.fintech.tests.e2e.FintechStagesUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;


@JGivenStage
public class UserInformationResult extends Stage<UserInformationResult> {

    @Autowired
    private UserRepository users;

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
        assertThat(users.findByXsrfToken(xsrfToken)).isNotEmpty();
        return self();
    }

    @SneakyThrows
    public UserInformationResult fintech_log_out_user() {
        users.deleteByXsrfToken(xsrfToken);
        assertThat(users.findByXsrfToken(xsrfToken)).isEqualTo(null);
        return self();
    }

    @SneakyThrows
    public UserInformationResult fintech_can_read_user_data_using_xsrfToken(
            boolean valideRessourceID) {
        ExtractableResponse<Response> response = withDefaultHeaders()
                                                         .header(X_XSRF_TOKEN, X_XSRF_TOKEN_VALUE)
                                                         .cookie(SESSION_COOKIE)
                                                         .when().get(FINTECH_LOGIN_ENDPOINT)
                                                         .then().statusCode(HttpStatus.OK.value())
                                                         .body("userProfile.name", equalTo(USERNAME))
                                                         .body("userProfile.lastLogin", equalTo(null))
                                                         .extract();
        this.responseContent = response.body().asString();
        return self();
    }

    @SneakyThrows
    public UserInformationResult fintech_can_read_bank_profile_using_xsrfToken() {
        return self();
    }

    private ExtractableResponse<Response> getBankSearchedFor(String bankName) {
        return withDefaultHeaders().header(SESSION_COOKIE, SESSION_COOKIE_VALUE)
                .queryParam("bankSearch", bankName)
                .when().get(BANKSEARCH_ENDPOINT, BANK_ID)
                .then().statusCode(HttpStatus.OK.value())
                .extract();
    }
}

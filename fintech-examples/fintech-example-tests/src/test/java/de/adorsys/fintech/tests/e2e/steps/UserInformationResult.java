package de.adorsys.fintech.tests.e2e.steps;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static de.adorsys.fintech.tests.e2e.steps.FintechStagesUtils.*;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class UserInformationResult extends Stage<UserInformationResult> {

    @Getter
    @ExpectedScenarioState
    private String responseContent;

    @SneakyThrows
    public UserInformationResult fintech_can_naviagte_to_bank_search_using_xsrfToken() {
        ExtractableResponse<Response> response = withDefaultHeaders()
                                                         .header(X_XSRF_TOKEN, X_XSRF_TOKEN_VALUE)
                                                         .cookie(SESSION_COOKIE, SESSION_COOKIE_VALUE)
                                                         .when().get(BANKSEARCH_ENDPOINT)
                                                         .then().statusCode(HttpStatus.OK.value())
                                                         .extract();
        this.responseContent = response.body().asString();
        return self();
    }

    @SneakyThrows
    public UserInformationResult fintech_can_read_bank_profile_using_xsrfToken() {
        ExtractableResponse<Response> response = withDefaultHeaders()
                                                         .header(X_XSRF_TOKEN, X_XSRF_TOKEN_VALUE)
                                                         .header(X_REQUEST_ID, UUID.randomUUID().toString())
                                                         .header(SESSION_COOKIE, SESSION_COOKIE_VALUE)
                                                         .queryParam("keyword", BANKSEARCH)
                                                         .when().get(BANKPROFILE_ENDPOINT +  BANK_ID_VALUE)
                                                         .then().statusCode(HttpStatus.OK.value())
                                                         .extract();
        this.responseContent = response.body().asString();
        return self();
    }
}

package de.adorsys.fintech.tests.e2e;

import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import static de.adorsys.fintech.tests.e2e.FintechStagesUtils.SESSION_COOKIE;
import static de.adorsys.fintech.tests.e2e.FintechStagesUtils.SESSION_COOKIE_VALUE;

@JGivenStage
@Slf4j
public class FintechServer<SELF extends FintechServer<SELF>> extends  WebDriverBasedUserInfoFintech<SELF> {

    @BeforeStage
    void prepareRestAssured() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public SELF user_is_logged_in() {
        RestAssured.given()
                .cookie(SESSION_COOKIE)
                .when().get("https://obg-dev-fintechserver.cloud.adorsys.de/v1/search/bankSearch")
                .then().statusCode(HttpStatus.OK.value());
        return self();
    }

    public SELF user_is_not_logged_in() {
        RestAssured.given()
                .cookie(SESSION_COOKIE).body("")
                .when().get("https://obg-dev-fintechserver.cloud.adorsys.de/v1/search/bankSearch")
                .then().statusCode(HttpStatus.UNAUTHORIZED.value());
        return self();
    }
}


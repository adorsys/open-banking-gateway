package de.adorsys.fintech.tests.e2e;

import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;

@JGivenStage
@Slf4j
public class FintechServer<SELF extends FintechServer<SELF>> extends  WebDriverBasedUserInfoFintech<SELF> {

    @BeforeStage
    void prepareRestAssured() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public SELF user_is_logged_in() {
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                .header(X_REQUEST_ID, UUID.randomUUID().toString())
                .body("bob")
                .when().get("https://obg-dev-fintechui.cloud.adorsys.de/search")
                .then().statusCode(HttpStatus.OK.value());
        return self();
    }

    public SELF user_is_not_logged_in() {
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(X_REQUEST_ID, UUID.randomUUID().toString())
                .body("{}")
                .when().get("https://obg-dev-fintechui.cloud.adorsys.de/login")
                .then().statusCode(HttpStatus.OK.value());
        return self();
    }
}


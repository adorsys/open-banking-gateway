package de.adorsys.fintech.tests.e2e.steps;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;

@JGivenStage
@Slf4j
public class FintechServer<SELF extends FintechServer<SELF>> extends Stage<SELF> {

    @Value("${test.webdriver.timeout}")
    private Duration timeout;

    @BeforeStage
    void prepareRestAssured() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public SELF fintech_points_to_fintechui_login_page(String fintechUri) {
        RestAssured.baseURI = fintechUri;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        return self();
    }

    public SELF fintech_point_to_another_page(WebDriver driver, String uri) {
        waitForPageLoadAndUrlEndsWithPath(driver, uri);
        return self();
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

    private void waitForPageLoadAndUrlEndsWithPath(WebDriver driver, String urlEndsWithPath) {
        new WebDriverWait(driver, timeout.getSeconds())
                .until(wd ->
                        URI.create(driver.getPageSource()).getPath().endsWith(urlEndsWithPath)
                                && ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete")
                );
    }
}


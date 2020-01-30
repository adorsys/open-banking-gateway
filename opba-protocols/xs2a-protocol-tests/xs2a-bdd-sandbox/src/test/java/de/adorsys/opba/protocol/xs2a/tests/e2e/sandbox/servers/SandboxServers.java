package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.config.RedirectConfig.redirectConfig;

@Slf4j
@JGivenStage
public class SandboxServers extends Stage<SandboxServers> {

    private static final String ASPSP_PROFILE_BASE_URI = "http://localhost:20010";

    @BeforeStage
    void prepareRestAssured() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssured.config().redirect(redirectConfig().followRedirects(false));
    }

    public void enabled_embedded_sandbox_mode() {
        RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("[\"EMBEDDED\",\"REDIRECT\",\"DECOUPLED\"]")
                .when()
                    .put(ASPSP_PROFILE_BASE_URI + "/api/v1/aspsp-profile/for-debug/sca-approaches")
                .then()
                    .statusCode(HttpStatus.OK.value());
    }

    public void enabled_redirect_sandbox_mode() {
        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("[\"REDIRECT\",\"EMBEDDED\",\"DECOUPLED\"]")
                .when()
                .put(ASPSP_PROFILE_BASE_URI + "/api/v1/aspsp-profile/for-debug/sca-approaches")
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}

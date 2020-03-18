package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers;

import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.CommonGivenStages;
import io.restassured.RestAssured;
import io.restassured.config.RedirectConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Slf4j
@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class SandboxServers<SELF extends SandboxServers<SELF>> extends CommonGivenStages<SELF> {

    private static final String ASPSP_PROFILE_BASE_URI = "http://localhost:20010";

    @BeforeStage
    void prepareRestAssured() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssured.config().redirect(RedirectConfig.redirectConfig().followRedirects(false));
    }

    public SELF enabled_embedded_sandbox_mode() {
        RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("[\"EMBEDDED\",\"REDIRECT\",\"DECOUPLED\"]")
                .when()
                    .put(ASPSP_PROFILE_BASE_URI + "/api/v1/aspsp-profile/for-debug/sca-approaches")
                .then()
                    .statusCode(HttpStatus.OK.value());

        return self();
    }

    public SELF enabled_redirect_sandbox_mode() {
        RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("[\"REDIRECT\",\"EMBEDDED\",\"DECOUPLED\"]")
                .when()
                    .put(ASPSP_PROFILE_BASE_URI + "/api/v1/aspsp-profile/for-debug/sca-approaches")
                .then()
                    .statusCode(HttpStatus.OK.value());

        return self();
    }
}

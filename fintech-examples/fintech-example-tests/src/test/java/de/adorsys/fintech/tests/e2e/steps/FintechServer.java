package de.adorsys.fintech.tests.e2e.steps;

import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.fintech.tests.e2e.config.SmokeConfig;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.SandboxServers;
import io.restassured.RestAssured;
import io.restassured.config.RedirectConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@JGivenStage
@Slf4j
public class FintechServer<SELF extends FintechServer<SELF>> extends SandboxServers<SELF> {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private SmokeConfig smokeConfig;

    @Autowired
    private BankProfileJpaRepository profiles;

    @BeforeStage
    void prepareRestAssured() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssured.config().redirect(RedirectConfig.redirectConfig().followRedirects(false));    }

    public SELF fintech_points_to_fintechui_login_page(String fintechUri) {
        RestAssured.baseURI = fintechUri;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        return self();
    }

    public SELF enabled_embedded_sandbox_mode() {
        return enabled_embedded_sandbox_mode(smokeConfig.getAspspProfileServerUri());
    }

    public SELF enabled_redirect_sandbox_mode() {
        return enabled_redirect_sandbox_mode(smokeConfig.getAspspProfileServerUri());
    }

    public SELF enabled_embedded_sandbox_mode(String aspspProfileUri) {
        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("[\"EMBEDDED\",\"REDIRECT\",\"DECOUPLED\"]")
                .when()
                .put(aspspProfileUri + "/api/v1/aspsp-profile/for-debug/sca-approaches")
                .then()
                .statusCode(HttpStatus.OK.value());

        return self();
    }

    public SELF enabled_redirect_sandbox_mode(String aspspProfileUri) {
        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("[\"REDIRECT\",\"EMBEDDED\",\"DECOUPLED\"]")
                .when()
                .put(aspspProfileUri + "/api/v1/aspsp-profile/for-debug/sca-approaches")
                .then()
                .statusCode(HttpStatus.OK.value());

        return self();
    }

    public SELF rest_assured_points_to_opba_server(String opbaServerUri) {
        RestAssured.baseURI = opbaServerUri;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        return self();
    }
}


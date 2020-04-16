package de.adorsys.fintech.tests.e2e.steps;

import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.db.domain.Approach;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.SandboxServers;
import io.restassured.RestAssured;
import io.restassured.config.RedirectConfig;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Duration;

import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;

@JGivenStage
@Slf4j
public class FintechServer<SELF extends FintechServer<SELF>> extends SandboxServers<SELF> {
    private static final String ASPSP_PROFILE_BASE_URI = "http://localhost:20010";

    @Value("${test.webdriver.timeout}")
    private Duration timeout;

    @LocalServerPort
    private int serverPort;

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

    private void waitForPageLoadAndUrlEndsWithPath(WebDriver driver, String urlEndsWithPath) {
        new WebDriverWait(driver, timeout.getSeconds())
                .until(wd ->
                        URI.create(driver.getPageSource()).getPath().endsWith(urlEndsWithPath)
                                && ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete")
                );
    }

    public SELF enabled_embedded_sandbox_mode() {
        return enabled_embedded_sandbox_mode(ASPSP_PROFILE_BASE_URI);
    }

    public SELF enabled_redirect_sandbox_mode() {
        return enabled_redirect_sandbox_mode(ASPSP_PROFILE_BASE_URI);
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

    @Transactional
    public SELF preferred_sca_approach_selected_for_all_banks_in_opba(Approach approach) {
        profiles.findAll().stream()
                .map(it -> {
                    it.setPreferredApproach(approach);
                    return it;
                })
                .forEach(profiles::save);

        return self();
    }

    public SELF rest_assured_points_to_opba_server() {
        return rest_assured_points_to_opba_server("http://localhost:" + serverPort);
    }

    public SELF rest_assured_points_to_opba_server(String opbaServerUri) {
        RestAssured.baseURI = opbaServerUri;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        config = config().redirect(redirectConfig().followRedirects(false));

        return self();
    }
}


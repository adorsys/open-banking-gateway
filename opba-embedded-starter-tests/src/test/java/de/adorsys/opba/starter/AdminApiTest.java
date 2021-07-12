package de.adorsys.opba.starter;

import com.google.common.io.Resources;
import de.adorsys.opba.starter.config.FintechRequestSigningTestConfig;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasToString;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * This is a very basic test to ensure that Admin API functions properly.
 */
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {OpenBankingEmbeddedApplication.class, FintechRequestSigningTestConfig.class}, webEnvironment = RANDOM_PORT)
class AdminApiTest {

    private static final String ADMIN_API = "/admin/v1/";
    private static final String BASIC_AUTH = "Basic QWxhZGRpbjpPcGVuU2VzYW1l";

    @LocalServerPort
    private int serverPort;

    private RequestSpecification withBasic;

    @BeforeEach
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost:" + serverPort;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        config = config().redirect(redirectConfig().followRedirects(false));
        withBasic = RestAssured.given().header(AUTHORIZATION, BASIC_AUTH).when();
    }

    @Test
    void getAdminBanks() {
        withBasic
                    .get(ADMIN_API + "banks")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size", equalTo(20))
                    .body("number", equalTo(0))
                    .body("totalElements", greaterThan(0))
                    .body("content.uuid", hasItem(hasToString("918d80fa-f7fd-4c9f-a6bd-7a9e12aeee76")));
    }

    @Test
    void getAdminBanksById() throws Exception {
        String body = withBasic
                    .get(ADMIN_API + "banks/918d80fa-f7fd-4c9f-a6bd-7a9e12aeee76")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .body()
                    .asString();

        JSONAssert.assertEquals(fixture("918d80fa-f7fd-4c9f-a6bd-7a9e12aeee76"), body, JSONCompareMode.LENIENT);
    }

    @SneakyThrows
    private String fixture(String name) {
        return Resources.toString(Resources.getResource("adminapi/" + name + ".json"), StandardCharsets.UTF_8);
    }
}

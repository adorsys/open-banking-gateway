package de.adorsys.opba.starter;

import de.adorsys.opba.starter.config.FintechRequestSigningTestConfig;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * This is a very basic test to ensure that Admin API is secured properly.
 */
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {OpenBankingEmbeddedApplication.class, FintechRequestSigningTestConfig.class}, webEnvironment = RANDOM_PORT)
class AdminApiSecurityTest {

    private static final String ADMIN_API = "/admin/";
    private static final String WRONG_BASIC_AUTH = "Basic QWxh";

    @LocalServerPort
    private int serverPort;

    @BeforeEach
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost:" + serverPort;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        config = config().redirect(redirectConfig().followRedirects(false));
    }

    @Test
    void getAdminBanksProtected() {
        RestAssured.when()
                    .get(ADMIN_API + "banks")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());

        RestAssured
                .given()
                    .header(AUTHORIZATION, WRONG_BASIC_AUTH)
                .when()
                    .get(ADMIN_API + "banks")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void getAdminBanksByIdProtected() {
        RestAssured.when()
                    .get(ADMIN_API + "banks/ff81f61f-f753-465b-8310-1066eb0eecd3")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());

        RestAssured
                .given()
                    .header(AUTHORIZATION, WRONG_BASIC_AUTH)
                .when()
                    .get(ADMIN_API + "banks/ff81f61f-f753-465b-8310-1066eb0eecd3")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void putAdminBanksByIdProtected() {
        RestAssured.when()
                    .put(ADMIN_API + "banks/ff81f61f-f753-465b-8310-1066eb0eecd3")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());

        RestAssured
                .given()
                    .header(AUTHORIZATION, WRONG_BASIC_AUTH)
                .when()
                    .put(ADMIN_API + "banks/ff81f61f-f753-465b-8310-1066eb0eecd3")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void patchAdminBanksByIdProtected() {
        RestAssured.when()
                    .patch(ADMIN_API + "banks/ff81f61f-f753-465b-8310-1066eb0eecd3")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());

        RestAssured
                .given()
                    .header(AUTHORIZATION, WRONG_BASIC_AUTH)
                .when()
                    .patch(ADMIN_API + "banks/ff81f61f-f753-465b-8310-1066eb0eecd3")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void deleteAdminBanksByIdProtected() {
        RestAssured.when()
                    .delete(ADMIN_API + "banks/ff81f61f-f753-465b-8310-1066eb0eecd3")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());

        RestAssured
                .given()
                    .header(AUTHORIZATION, WRONG_BASIC_AUTH)
                .when()
                    .delete(ADMIN_API + "banks/ff81f61f-f753-465b-8310-1066eb0eecd3")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}

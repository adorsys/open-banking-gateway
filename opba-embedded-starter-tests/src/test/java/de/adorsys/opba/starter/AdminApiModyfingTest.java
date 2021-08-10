package de.adorsys.opba.starter;

import com.google.common.io.Resources;
import de.adorsys.opba.starter.config.FintechRequestSigningTestConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * This is a very basic test to ensure that Admin API functions properly.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = {"test", "test-separate-db"})  // Use clean DB as may collide
@SpringBootTest(classes = {OpenBankingEmbeddedApplication.class, FintechRequestSigningTestConfig.class}, webEnvironment = RANDOM_PORT)
class AdminApiModyfingTest {

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
    void putAdminBanksByIdProtected() throws Exception {
        String updated = fixture("20a55c80-fd8c-42f0-b782-8188662aa89f-new");

        withBasic
                    .contentType(ContentType.JSON)
                    .body(updated)
                    .put(ADMIN_API + "banks/20a55c80-fd8c-42f0-b782-8188662aa89f")
                .then()
                    .statusCode(HttpStatus.OK.value());

        String body = withBasic
                    .get(ADMIN_API + "banks/20a55c80-fd8c-42f0-b782-8188662aa89f")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .body()
                    .asString();

        JSONAssert.assertEquals(updated, body, JSONCompareMode.LENIENT);

    }

    @Test
    void patchAdminBanksByIdProfilesRemoved() throws Exception {
        String updated = fixture("adadadad-1000-0000-0000-b0b0b0b0b0b0-profiles-removed");

        withBasic
                    .contentType(ContentType.JSON)
                    .body(updated)
                    .patch(ADMIN_API + "banks/adadadad-1000-0000-0000-b0b0b0b0b0b0")
                .then()
                    .statusCode(HttpStatus.OK.value());

        String body = withBasic
                    .get(ADMIN_API + "banks/adadadad-1000-0000-0000-b0b0b0b0b0b0")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .body()
                    .asString();

        JSONAssert.assertEquals(updated.replaceAll("\"profiles\": \\[],", ""), body, JSONCompareMode.LENIENT);
    }

    @Test
    void patchAdminBanksByIdProfileReplaced() throws Exception {
        String updated = fixture("adadadad-1000-0000-0000-b0b0b0b0b0b0-profiles-replaced");

        withBasic
                .contentType(ContentType.JSON)
                .body(updated)
                .patch(ADMIN_API + "banks/adadadad-1000-0000-0000-b0b0b0b0b0b0")
                .then()
                .statusCode(HttpStatus.OK.value());

        String body = withBasic
                .get(ADMIN_API + "banks/adadadad-1000-0000-0000-b0b0b0b0b0b0")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .asString();

        JSONAssert.assertEquals(updated, body, JSONCompareMode.LENIENT);
    }

    @Test
    void patchAdminBanksByIdProtected() throws Exception {
        String updated = fixture("adadadad-1000-0000-0000-b0b0b0b0b0b0-updated");

        withBasic
                .contentType(ContentType.JSON)
                .body(updated)
                .patch(ADMIN_API + "banks/adadadad-1000-0000-0000-b0b0b0b0b0b0")
                .then()
                .statusCode(HttpStatus.OK.value());

        String body = withBasic
                .get(ADMIN_API + "banks/adadadad-1000-0000-0000-b0b0b0b0b0b0")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .asString();

        JSONAssert.assertEquals(updated, body, JSONCompareMode.LENIENT);
    }

    @Test
    void deleteAdminBanksByIdProtected() {
        withBasic
                    .delete(ADMIN_API + "banks/adadadad-3000-0000-0000-b0b0b0b0b0b0")
                .then()
                    .statusCode(HttpStatus.OK.value());

        withBasic
                    .get(ADMIN_API + "banks/adadadad-3000-0000-0000-b0b0b0b0b0b0")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @SneakyThrows
    private String fixture(String name) {
        return Resources.toString(Resources.getResource("adminapi/" + name + ".json"), StandardCharsets.UTF_8);
    }
}

package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.SERVICE_SESSION_PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_TIMESTAMP_UTC;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.DEFAULT_FINTECH_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AIS_AUTH_SESSION_STATUS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.PIS_AUTH_SESSION_STATUS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.SESSION_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class RequestStatusUtil {

    public static final String STARTED = "STARTED";
    public static final String ACTIVATED = "ACTIVATED";
    public static final String PENDING = "PENDING";

    static void fintechCallsAisAuthorizationSessionState(String expectedSessionState, String expectedAuthSessionState, String serviceSessionId) {
        ExtractableResponse<Response> response = RestAssured
                .given()
                    .header(SERVICE_SESSION_PASSWORD, SESSION_PASSWORD)
                    .header(FINTECH_ID, DEFAULT_FINTECH_ID)
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .header(X_TIMESTAMP_UTC, Instant.now().toString())
                .when()
                    .get(AIS_AUTH_SESSION_STATUS_ENDPOINT, serviceSessionId)
                .then()
                    .statusCode(OK.value())
                .extract();

        assertThat(response.body().jsonPath().getString("status")).isEqualTo(expectedSessionState);
        assertThat(response.body().jsonPath().getString("detailedStatus.values()[0].status")).isEqualTo(expectedAuthSessionState);
    }

    static void fintechCallsPisAuthorizationSessionState(String expectedSessionState, String expectedAuthSessionState, String serviceSessionId) {
        ExtractableResponse<Response> response = RestAssured
                .given()
                    .header(SERVICE_SESSION_PASSWORD, SESSION_PASSWORD)
                    .header(FINTECH_ID, DEFAULT_FINTECH_ID)
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .header(X_TIMESTAMP_UTC, Instant.now().toString())
                .when()
                    .get(PIS_AUTH_SESSION_STATUS_ENDPOINT, serviceSessionId)
                .then()
                    .statusCode(OK.value())
                .extract();

        assertThat(response.body().jsonPath().getString("status")).isEqualTo(expectedSessionState);
        assertThat(response.body().jsonPath().getString("detailedStatus.values()[0].status")).isEqualTo(expectedAuthSessionState);
    }
}

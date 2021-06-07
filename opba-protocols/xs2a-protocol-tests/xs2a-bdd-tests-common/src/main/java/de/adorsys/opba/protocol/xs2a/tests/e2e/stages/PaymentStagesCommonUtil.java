package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.BANK_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_REDIRECT_URL_NOK;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_REDIRECT_URL_OK;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_USER_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.SERVICE_SESSION_PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_PSU_AUTHENTICATION_REQUIRED;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_TIMESTAMP_UTC;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.SANDBOX_BANK_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_IP_ADDRESS;

@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor", "PMD.AvoidUsingHardCodedIP"})
public class PaymentStagesCommonUtil {

    public static final String INITIATE_PAYMENT_ENDPOINT = "/v1/banking/pis/payments/{payment-product}";
    public static final String PIS_LOGIN_USER_ENDPOINT = "/v1/psu/{authorizationId}/for-approval/login";
    public static final String PIS_ANONYMOUS_LOGIN_USER_ENDPOINT = "/v1/psu/{authorizationId}/for-approval/anonymous";
    public static final String GET_PAYMENT_AUTH_STATE = "/v1/consent/{serviceSessionId}";
    public static final String AUTHORIZE_PAYMENT_ENDPOINT = "/v1/consent/{serviceSessionId}/embedded";

    public static final String SEPA_PAYMENT = "sepa-credit-transfers";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";

    public static final String DEFAULT_FINTECH_ID = "MY-SUPER-FINTECH-ID";
    public static final String FINTECH_REDIR_OK = "http://localhost:4444/redirect-after-consent";
    public static final String FINTECH_REDIR_NOK = "http://localhost:4444/redirect-after-consent-denied";
    public static final String XSRF_TOKEN = "abc123.faadsf93nlas32wx";

    public static final String ANTON_BRUECKNER = "anton.brueckner";
    public static final String SESSION_PASSWORD = "qwerty";
    public static final String IP_ADDRESS = "1.1.1.1";

    public static RequestSpecification withPaymentHeaders(String fintechUserId) {
        return withPaymentHeaders(fintechUserId, SANDBOX_BANK_ID, true);
    }

    public static RequestSpecification withPaymentHeaders(
            String fintechUserId,
            boolean psuAuthenticationRequired
    ) {
        return withPaymentHeaders(fintechUserId, SANDBOX_BANK_ID, psuAuthenticationRequired);
    }

    public static RequestSpecification withPaymentHeaders(
            String fintechUserId,
            String bankId,
            boolean psuAuthenticationRequired
    ) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return RestAssured
            .given()
                .header(BANK_ID, bankId)
                .header(FINTECH_REDIRECT_URL_OK, FINTECH_REDIR_OK)
                .header(FINTECH_REDIRECT_URL_NOK, FINTECH_REDIR_NOK)
                .header(SERVICE_SESSION_PASSWORD, SESSION_PASSWORD)
                .header(FINTECH_USER_ID, fintechUserId)
                .header(FINTECH_ID, DEFAULT_FINTECH_ID)
                .header(X_XSRF_TOKEN, XSRF_TOKEN)
                .header(X_REQUEST_ID, xRequestId.toString())
                .header(X_TIMESTAMP_UTC, xTimestampUtc.toString())
                .header(X_PSU_AUTHENTICATION_REQUIRED, psuAuthenticationRequired)
                .header(PSU_IP_ADDRESS, IP_ADDRESS);
    }

    public static RequestSpecification withPaymentInfoHeaders(String fintechUserId) {
        return withPaymentInfoHeaders(fintechUserId, SANDBOX_BANK_ID);
    }

    public static RequestSpecification withPaymentInfoHeaders(String fintechUserId, String bankId) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return RestAssured
            .given()
                .header(BANK_ID, bankId)
                .header(SERVICE_SESSION_PASSWORD, SESSION_PASSWORD)
                .header(FINTECH_USER_ID, fintechUserId)
                .header(FINTECH_ID, DEFAULT_FINTECH_ID)
                .header(X_XSRF_TOKEN, XSRF_TOKEN)
                .header(X_REQUEST_ID, xRequestId.toString())
                .header(X_TIMESTAMP_UTC, xTimestampUtc.toString())
                .header(PSU_IP_ADDRESS, IP_ADDRESS);
    }
}

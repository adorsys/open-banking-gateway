package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.api.security.external.domain.signdata.GetPaymentDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.PaymentInitiationDataToSign;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
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
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_OPERATION_TYPE;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_PIS_PSU_AUTHENTICATION_REQUIRED;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_SIGNATURE;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_TIMESTAMP_UTC;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_IP_ADDRESS;

@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor", "PMD.AvoidUsingHardCodedIP"})
public class PaymentStagesCommonUtil {

    public static final String INITIATE_PAYMENT_ENDPOINT = "/v1/banking/pis/payments/{payment-product}";
    public static final String PIS_LOGIN_USER_ENDPOINT = "/v1/psu/pis/{authorizationId}/for-approval/login";
    public static final String PIS_ANONYMOUS_LOGIN_USER_ENDPOINT = "/v1/psu/pis/{authorizationId}/anonymous";
    public static final String GET_PAYMENT_AUTH_STATE = "/v1/consent/{serviceSessionId}";
    public static final String AUTHORIZE_PAYMENT_ENDPOINT = "/v1/consent/{serviceSessionId}/embedded";

    public static final String SEPA_PAYMENT = "sepa-credit-transfers";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";

    public static final String DEFAULT_FINTECH_ID = "MY-SUPER-FINTECH-ID";
    public static final String SANDBOX_BANK_ID = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
    public static final String FINTECH_REDIR_OK = "http://localhost:4444/redirect-after-consent";
    public static final String FINTECH_REDIR_NOK = "http://localhost:4444/redirect-after-consent-denied";
    public static final String XSRF_TOKEN = "abc123.faadsf93nlas32wx";

    public static final String ANTON_BRUECKNER = "anton.brueckner";
    public static final String SESSION_PASSWORD = "qwerty";
    public static final String IP_ADDRESS = "1.1.1.1";

    public static RequestSpecification withPaymentHeaders(
            String fintechUserId,
            RequestSigningService requestSigningService,
            OperationType operationType,
            String body
    ) {
        return withPaymentHeaders(fintechUserId, SANDBOX_BANK_ID, requestSigningService, operationType, body, true);
    }

    public static RequestSpecification withPaymentHeaders(
            String fintechUserId,
            RequestSigningService requestSigningService,
            OperationType operationType,
            String body,
            boolean psuAuthenticationRequired
    ) {
        return withPaymentHeaders(fintechUserId, SANDBOX_BANK_ID, requestSigningService, operationType, body, psuAuthenticationRequired);
    }

    public static RequestSpecification withPaymentHeaders(
            String fintechUserId,
            String bankId,
            RequestSigningService requestSigningService,
            OperationType operationType,
            String body
    ) {
        return withPaymentHeaders(fintechUserId, bankId, requestSigningService, operationType, body, true);
    }

    public static RequestSpecification withPaymentHeaders(
            String fintechUserId,
            String bankId,
            RequestSigningService requestSigningService,
            OperationType operationType,
            String body,
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
                       .header(X_OPERATION_TYPE, operationType)
                       .header(X_PIS_PSU_AUTHENTICATION_REQUIRED, psuAuthenticationRequired)
                       .header(X_REQUEST_SIGNATURE, calculatePaymentSignature(requestSigningService, xRequestId, xTimestampUtc, operationType, fintechUserId, psuAuthenticationRequired, body, bankId))
                       .header(PSU_IP_ADDRESS, IP_ADDRESS);
    }

    public static RequestSpecification withPaymentInfoHeaders(String fintechUserId, RequestSigningService requestSigningService, OperationType operationType) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return RestAssured
                       .given()
                       .header(BANK_ID, SANDBOX_BANK_ID)
                       .header(SERVICE_SESSION_PASSWORD, SESSION_PASSWORD)
                       .header(FINTECH_USER_ID, fintechUserId)
                       .header(FINTECH_ID, DEFAULT_FINTECH_ID)
                       .header(X_XSRF_TOKEN, XSRF_TOKEN)
                       .header(X_REQUEST_ID, xRequestId.toString())
                       .header(X_TIMESTAMP_UTC, xTimestampUtc.toString())
                       .header(X_OPERATION_TYPE, operationType)
                       .header(X_REQUEST_SIGNATURE, calculatePaymentInfoSignature(requestSigningService, xRequestId, xTimestampUtc, operationType, fintechUserId))
                       .header(PSU_IP_ADDRESS, IP_ADDRESS);
    }

    private static String calculatePaymentSignature(RequestSigningService requestSigningService, UUID xRequestId, Instant xTimestampUtc,
                                                    OperationType operationType, String fintechUserId, boolean psuAuthenticationRequired, String body, String bankId) {
        PaymentInitiationDataToSign paymentInitiationDataToSign = PaymentInitiationDataToSign.builder()
                                                                          .xRequestId(xRequestId)
                                                                          .instant(xTimestampUtc)
                                                                          .operationType(operationType)
                                                                          .bankId(bankId)
                                                                          .fintechUserId(fintechUserId)
                                                                          .redirectOk(FINTECH_REDIR_OK)
                                                                          .redirectNok(FINTECH_REDIR_NOK)
                                                                          .psuAuthenticationRequired(String.valueOf(psuAuthenticationRequired))
                                                                          .body(body)
                                                                          .build();

        return requestSigningService.signature(paymentInitiationDataToSign);
    }

    private static String calculatePaymentInfoSignature(RequestSigningService requestSigningService, UUID xRequestId, Instant xTimestampUtc,
                                                        OperationType operationType, String fintechUserId) {
        GetPaymentDataToSign getPaymentDataToSign = GetPaymentDataToSign.builder()
                                             .xRequestId(xRequestId)
                                             .instant(xTimestampUtc)
                                             .operationType(operationType)
                                             .bankId(SANDBOX_BANK_ID)
                                             .fintechUserId(fintechUserId)
                                             .build();

        return requestSigningService.signature(getPaymentDataToSign);
    }
}

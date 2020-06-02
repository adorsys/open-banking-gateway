package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.api.security.external.domain.signdata.AisListAccountsDataToSign;
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
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_SIGNATURE;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_TIMESTAMP_UTC;
import static de.adorsys.opba.restapi.shared.HttpHeaders.COMPUTE_PSU_IP_ADDRESS;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_IP_ADDRESS;

@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor", "PMD.AvoidUsingHardCodedIP"})
public class PisStagesCommonUtil {

    public static final String INITIATE_PAYMENT_ENDPOINT = "/v1/banking/pis/payments/{payment-product}";

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";

    public static final String DEFAULT_FINTECH_ID = "MY-SUPER-FINTECH-ID";
    public static final String SANDBOX_BANK_ID = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
    public static final String FINTECH_REDIR_OK = "http://localhost:4444/redirect-after-consent";
    public static final String FINTECH_REDIR_NOK = "http://localhost:4444/redirect-after-consent-denied";

    public static final String ANTON_BRUECKNER = "anton.brueckner";
    public static final String SESSION_PASSWORD = "qwerty";
    public static final String COMPUTE_IP_ADDRESS = "false";
    public static final String IP_ADDRESS = "1.1.1.1";

    public static RequestSpecification withPaymentHeaders(String fintechUserId, RequestSigningService requestSigningService, OperationType operationType) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return withSignedHeadersWithoutIpAddress(fintechUserId, operationType, xRequestId, xTimestampUtc)
                       .header(X_REQUEST_SIGNATURE, calculatePaymentSignature(requestSigningService, xRequestId, xTimestampUtc, operationType, fintechUserId))
                       .header(COMPUTE_PSU_IP_ADDRESS, COMPUTE_IP_ADDRESS)
                       .header(PSU_IP_ADDRESS, IP_ADDRESS);
    }

    private static RequestSpecification withSignedHeadersWithoutIpAddress(String fintechUserId, OperationType operationType, UUID xRequestId, Instant xTimestampUtc) {
        return RestAssured
                       .given()
                       .header(BANK_ID, SANDBOX_BANK_ID)
                       .header(FINTECH_REDIRECT_URL_OK, FINTECH_REDIR_OK)
                       .header(FINTECH_REDIRECT_URL_NOK, FINTECH_REDIR_NOK)
                       .header(SERVICE_SESSION_PASSWORD, SESSION_PASSWORD)
                       .header(FINTECH_USER_ID, fintechUserId)
                       .header(FINTECH_ID, DEFAULT_FINTECH_ID)
                       .header(X_REQUEST_ID, xRequestId.toString())
                       .header(X_TIMESTAMP_UTC, xTimestampUtc.toString())
                       .header(X_OPERATION_TYPE, operationType);
    }

    private static String calculatePaymentSignature(RequestSigningService requestSigningService, UUID xRequestId, Instant xTimestampUtc,
                                                    OperationType operationType, String fintechUserId) {
        PaymentInitiationDataToSign aymentInitiationDataToSign = new PaymentInitiationDataToSign(xRequestId, xTimestampUtc, operationType,
                                                                                                           SANDBOX_BANK_ID, fintechUserId, FINTECH_REDIR_OK, FINTECH_REDIR_NOK);
        return requestSigningService.signature(aymentInitiationDataToSign);
    }
}

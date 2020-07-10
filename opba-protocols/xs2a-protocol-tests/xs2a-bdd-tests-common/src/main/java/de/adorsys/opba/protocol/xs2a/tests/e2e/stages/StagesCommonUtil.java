package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.api.security.external.domain.signdata.AisListAccountsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.AisListTransactionsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.ConfirmConsentDataToSign;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.protocol.xs2a.tests.GetTransactionsQueryParams;
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
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor", "PMD.AvoidUsingHardCodedIP"}) //Checkstyle doesn't recognise Lombok
public class StagesCommonUtil {

    public static final String GET_CONSENT_AUTH_STATE = "/v1/consent/{serviceSessionId}";
    public static final String DENY_CONSENT_AUTH_ENDPOINT = "/v1/consent/{serviceSessionId}/deny";
    public static final String AUTHORIZE_CONSENT_ENDPOINT = "/v1/consent/{serviceSessionId}/embedded";
    public static final String AIS_ACCOUNTS_ENDPOINT = "/v1/banking/ais/accounts";
    public static final String AIS_TRANSACTIONS_ENDPOINT = "/v1/banking/ais/accounts/{resourceId}/transactions";
    public static final String AIS_TRANSACTIONS_WITHOUT_RESOURCE_ID_ENDPOINT = "/v1/banking/ais/transactions";
    public static final String PIS_SINGLE_PAYMENT_ENDPOINT = "/v1/banking/pis/payments/{payment-product}";
    public static final String PIS_PAYMENT_STATUS_ENDPOINT = "/v1/banking/pis/payments/{payment_product}/status";
    public static final String PIS_PAYMENT_INFORMATION_ENDPOINT = "/v1/banking/pis/payments/{payment_product}";

    public static final String REGISTER_USER_ENDPOINT = "/v1/psu/register";
    public static final String AIS_LOGIN_USER_ENDPOINT = "/v1/psu/ais/{authorizationId}/for-approval/login";
    public static final String CONFIRM_CONSENT_ENDPOINT = "/v1/banking/consents/{authorizationId}/confirm";
    public static final String PIS_LOGIN_USER_ENDPOINT = "/v1/psu/pis/{authorizationId}/for-approval/login";

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";

    public static final String DEFAULT_FINTECH_ID = "MY-SUPER-FINTECH-ID";
    public static final String SANDBOX_BANK_ID = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
    public static final String FINTECH_REDIR_OK = "http://localhost:4444/redirect-after-consent";
    public static final String FINTECH_REDIR_NOK = "http://localhost:4444/redirect-after-consent-denied";

    public static final String SESSION_PASSWORD = "qwerty";
    public static final String ANTON_BRUECKNER = "anton.brueckner";
    public static final String MAX_MUSTERMAN = "max.musterman";
    public static final String COMPUTE_IP_ADDRESS = "false";
    public static final String IP_ADDRESS = "1.1.1.1";

    public static RequestSpecification withAccountsHeaders(String fintechUserId, RequestSigningService requestSigningService, OperationType operationType) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return headersWithoutIpAddress(fintechUserId, operationType, xRequestId, xTimestampUtc)
                       .header(X_REQUEST_SIGNATURE, calculateAccountsSignature(requestSigningService, SANDBOX_BANK_ID, xRequestId, xTimestampUtc, operationType, fintechUserId))
                       .header(COMPUTE_PSU_IP_ADDRESS, COMPUTE_IP_ADDRESS)
                       .header(PSU_IP_ADDRESS, IP_ADDRESS);
    }

    public static RequestSpecification withAccountsHeaders(String fintechUserId, String bankId, RequestSigningService requestSigningService, OperationType operationType) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return headersWithoutIpAddress(fintechUserId, bankId, operationType, xRequestId, xTimestampUtc)
                .header(X_REQUEST_SIGNATURE, calculateAccountsSignature(requestSigningService, bankId, xRequestId, xTimestampUtc, operationType, fintechUserId))
                .header(COMPUTE_PSU_IP_ADDRESS, COMPUTE_IP_ADDRESS)
                .header(PSU_IP_ADDRESS, IP_ADDRESS);
    }

    public static RequestSpecification withAccountsHeadersMissingIpAddress(String fintechUserId, RequestSigningService requestSigningService, OperationType operationType) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return headersWithoutIpAddress(fintechUserId, operationType, xRequestId, xTimestampUtc)
                       .header(X_REQUEST_SIGNATURE, calculateAccountsSignature(requestSigningService, SANDBOX_BANK_ID, xRequestId, xTimestampUtc, operationType, fintechUserId))
                       .header(COMPUTE_PSU_IP_ADDRESS, COMPUTE_IP_ADDRESS);
    }

    public static RequestSpecification withTransactionsHeaders(String fintechUserId, RequestSigningService requestSigningService, OperationType operationType, GetTransactionsQueryParams params) {
        return withTransactionsHeaders(fintechUserId, SANDBOX_BANK_ID, requestSigningService, operationType, params);
    }

    public static RequestSpecification withTransactionsHeaders(
            String fintechUserId,
            String bankId,
            RequestSigningService requestSigningService,
            OperationType operationType,
            GetTransactionsQueryParams params
    ) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return headersWithoutIpAddress(fintechUserId, bankId, operationType, xRequestId, xTimestampUtc)
                       .header(X_REQUEST_SIGNATURE, calculateTransactionsSignature(requestSigningService, bankId, xRequestId, xTimestampUtc, operationType, fintechUserId, params))
                       .header(COMPUTE_PSU_IP_ADDRESS, COMPUTE_IP_ADDRESS)
                       .header(PSU_IP_ADDRESS, IP_ADDRESS);
    }

    public static RequestSpecification withDefaultHeaders(String fintechUserId, RequestSigningService requestSigningService, OperationType operationType) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return headersWithoutIpAddress(fintechUserId, operationType, xRequestId, xTimestampUtc)
                       .header(X_REQUEST_SIGNATURE, calculateConfirmConsentSignature(requestSigningService, xRequestId, xTimestampUtc, operationType))
                       .header(COMPUTE_PSU_IP_ADDRESS, COMPUTE_IP_ADDRESS)
                       .header(PSU_IP_ADDRESS, IP_ADDRESS);
    }

    public static RequestSpecification withSignatureHeaders(RequestSpecification specification, RequestSigningService requestSigningService, OperationType operationType) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return specification
                       .header(FINTECH_ID, DEFAULT_FINTECH_ID)
                       .header(X_REQUEST_ID, xRequestId.toString())
                       .header(X_TIMESTAMP_UTC, xTimestampUtc.toString())
                       .header(X_OPERATION_TYPE, operationType)
                       .header(X_REQUEST_SIGNATURE, calculateConfirmConsentSignature(requestSigningService, xRequestId, xTimestampUtc, operationType));
    }

    private static String calculateAccountsSignature(RequestSigningService requestSigningService, String bankId, UUID xRequestId, Instant xTimestampUtc,
                                                     OperationType operationType, String fintechUserId) {
        AisListAccountsDataToSign aisListAccountsDataToSign = AisListAccountsDataToSign.builder()
                                                                      .xRequestId(xRequestId)
                                                                      .instant(xTimestampUtc)
                                                                      .operationType(operationType)
                                                                      .bankId(bankId)
                                                                      .fintechUserId(fintechUserId)
                                                                      .redirectOk(FINTECH_REDIR_OK)
                                                                      .redirectNok(FINTECH_REDIR_NOK)
                                                                      .build();
        return requestSigningService.signature(aisListAccountsDataToSign);
    }

    private static String calculateTransactionsSignature(RequestSigningService requestSigningService, String bankId, UUID xRequestId, Instant xTimestampUtc,
                                                         OperationType operationType, String fintechUserId, GetTransactionsQueryParams params) {
        AisListTransactionsDataToSign aisListTransactionsDataToSign = AisListTransactionsDataToSign.builder()
                                                                              .xRequestId(xRequestId)
                                                                              .instant(xTimestampUtc)
                                                                              .operationType(operationType)
                                                                              .bankId(bankId)
                                                                              .fintechUserId(fintechUserId)
                                                                              .redirectOk(FINTECH_REDIR_OK)
                                                                              .redirectNok(FINTECH_REDIR_NOK)
                                                                              .dateFrom(params.getDateFrom())
                                                                              .dateTo(params.getDateTo())
                                                                              .entryReferenceFrom(params.getEntryReferenceFrom())
                                                                              .bookingStatus(params.getBookingStatus())
                                                                              .deltaList(params.getDeltaList())
                                                                              .build();
        return requestSigningService.signature(aisListTransactionsDataToSign);
    }

    private static String calculateConfirmConsentSignature(RequestSigningService requestSigningService, UUID xRequestId, Instant xTimestampUtc, OperationType operationType) {
        ConfirmConsentDataToSign aisListAccountsDataToSign = new ConfirmConsentDataToSign(xRequestId, xTimestampUtc, operationType);
        return requestSigningService.signature(aisListAccountsDataToSign);
    }

    private static RequestSpecification headersWithoutIpAddress(String fintechUserId, OperationType operationType, UUID xRequestId, Instant xTimestampUtc) {
        return headersWithoutIpAddress(fintechUserId, SANDBOX_BANK_ID, operationType, xRequestId, xTimestampUtc);
    }

    private static RequestSpecification headersWithoutIpAddress(String fintechUserId, String bankId, OperationType operationType, UUID xRequestId, Instant xTimestampUtc) {
        return RestAssured
                       .given()
                       .header(BANK_ID, bankId)
                       .header(FINTECH_REDIRECT_URL_OK, FINTECH_REDIR_OK)
                       .header(FINTECH_REDIRECT_URL_NOK, FINTECH_REDIR_NOK)
                       .header(SERVICE_SESSION_PASSWORD, SESSION_PASSWORD)
                       .header(FINTECH_USER_ID, fintechUserId)
                       .header(FINTECH_ID, DEFAULT_FINTECH_ID)
                       .header(X_REQUEST_ID, xRequestId.toString())
                       .header(X_TIMESTAMP_UTC, xTimestampUtc.toString())
                       .header(X_OPERATION_TYPE, operationType);
    }
}

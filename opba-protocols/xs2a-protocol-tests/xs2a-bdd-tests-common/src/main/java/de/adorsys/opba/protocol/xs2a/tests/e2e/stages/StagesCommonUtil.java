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
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_TIMESTAMP_UTC;
import static de.adorsys.opba.restapi.shared.HttpHeaders.COMPUTE_PSU_IP_ADDRESS;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_IP_ADDRESS;

@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor", "PMD.AvoidUsingHardCodedIP"})
//Checkstyle doesn't recognise Lombok
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
    public static final String CONFIRM_PAYMENT_ENDPOINT = "/v1/banking/payments/{authorizationId}/confirm";
    public static final String PIS_LOGIN_USER_ENDPOINT = "/v1/psu/pis/{authorizationId}/for-approval/login";

    public static final String TPP_MANAGEMENT_LOGIN_ENDPOINT = "/tpp/login";
    public static final String TPP_MANAGEMENT_CREATE_USER_ENDPOINT = "/tpp/users";
    public static final String TPP_MANAGEMENT_CREATE_ACCOUNT_ENDPOINT = "/tpp/accounts";
    public static final String TPP_MANAGEMENT_GET_ACCOUNT_DETAILS_ENDPOINT = "/tpp/accounts/details";
    public static final String TPP_MANAGEMENT_DEPOSIT_CASH_ENDPOINT = "/tpp/accounts/{accountId}/deposit-cash";
    public static final String TPP_MANAGEMENT_USER_ID_QUERY = "userId";
    public static final String TPP_MANAGEMENT_IBAN_QUERY = "iban";
    public static final String TPP_MANAGEMENT_LOGIN_HEADER = "login";
    public static final String TPP_MANAGEMENT_PASSWORD_HEADER = "pin";
    public static final String TPP_MANAGEMENT_AUTH_TOKEN = "access_token";
    public static final String TPP_MANAGEMENT_AUTH_HEADER = "authorization";

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";

    public static final String DEFAULT_FINTECH_ID = "MY-SUPER-FINTECH-ID";
    public static final String SANDBOX_BANK_ID = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
    public static final String SANDBOX_OAUTH2_INTEGRATED_BANK_ID = "867a53d8-4cca-4365-a393-7febb0bbd38e";
    public static final String FINTECH_REDIR_OK = "http://localhost:4444/redirect-after-consent";
    public static final String FINTECH_REDIR_NOK = "http://localhost:4444/redirect-after-consent-denied";

    public static final String SESSION_PASSWORD = "qwerty";
    public static final String ANTON_BRUECKNER = "anton.brueckner";
    public static final String MAX_MUSTERMAN = "max.musterman";
    public static final String COMPUTE_IP_ADDRESS = "false";
    public static final String IP_ADDRESS = "1.1.1.1";

    public static RequestSpecification withAccountsHeaders(String fintechUserId) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return headersWithoutIpAddress(fintechUserId, xRequestId, xTimestampUtc)
                .header(COMPUTE_PSU_IP_ADDRESS, COMPUTE_IP_ADDRESS)
                .header(PSU_IP_ADDRESS, IP_ADDRESS);
    }

    public static RequestSpecification withAccountsHeaders(String fintechUserId, String bankId) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return headersWithoutIpAddress(fintechUserId, bankId, xRequestId, xTimestampUtc)
                .header(COMPUTE_PSU_IP_ADDRESS, COMPUTE_IP_ADDRESS)
                .header(PSU_IP_ADDRESS, IP_ADDRESS);
    }

    public static RequestSpecification withAccountsHeadersMissingIpAddress(String fintechUserId) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return headersWithoutIpAddress(fintechUserId, xRequestId, xTimestampUtc)
                .header(COMPUTE_PSU_IP_ADDRESS, COMPUTE_IP_ADDRESS);
    }

    public static RequestSpecification withTransactionsHeaders(String fintechUserId) {
        return withTransactionsHeaders(fintechUserId, SANDBOX_BANK_ID);
    }

    public static RequestSpecification withTransactionsHeaders(
            String fintechUserId,
            String bankId
    ) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return headersWithoutIpAddress(fintechUserId, bankId, xRequestId, xTimestampUtc)
                .header(COMPUTE_PSU_IP_ADDRESS, COMPUTE_IP_ADDRESS)
                .header(PSU_IP_ADDRESS, IP_ADDRESS);
    }

    public static RequestSpecification withDefaultHeaders(String fintechUserId) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return headersWithoutIpAddress(fintechUserId, xRequestId, xTimestampUtc)
                .header(COMPUTE_PSU_IP_ADDRESS, COMPUTE_IP_ADDRESS)
                .header(PSU_IP_ADDRESS, IP_ADDRESS);
    }

    public static RequestSpecification withSignatureHeaders(RequestSpecification specification) {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        return specification
                .header(FINTECH_ID, DEFAULT_FINTECH_ID)
                .header(X_REQUEST_ID, xRequestId.toString())
                .header(X_TIMESTAMP_UTC, xTimestampUtc.toString());
    }

    private static RequestSpecification headersWithoutIpAddress(String fintechUserId, UUID xRequestId, Instant xTimestampUtc) {
        return headersWithoutIpAddress(fintechUserId, SANDBOX_BANK_ID, xRequestId, xTimestampUtc);
    }

    private static RequestSpecification headersWithoutIpAddress(String fintechUserId, String bankId, UUID xRequestId, Instant xTimestampUtc) {
        return RestAssured
                .given()
                .header(BANK_ID, bankId)
                .header(FINTECH_REDIRECT_URL_OK, FINTECH_REDIR_OK)
                .header(FINTECH_REDIRECT_URL_NOK, FINTECH_REDIR_NOK)
                .header(SERVICE_SESSION_PASSWORD, SESSION_PASSWORD)
                .header(FINTECH_USER_ID, fintechUserId)
                .header(FINTECH_ID, DEFAULT_FINTECH_ID)
                .header(X_REQUEST_ID, xRequestId.toString())
                .header(X_TIMESTAMP_UTC, xTimestampUtc.toString());
    }
}

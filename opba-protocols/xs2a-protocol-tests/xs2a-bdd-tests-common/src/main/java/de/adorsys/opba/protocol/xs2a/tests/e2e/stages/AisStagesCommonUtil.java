package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.UtilityClass;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.BANK_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_REDIRECT_URL_NOK;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_REDIRECT_URL_OK;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_SIGNATURE;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_TIMESTAMP_UTC;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.FINTECH_USER_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.SERVICE_SESSION_PASSWORD;
import static de.adorsys.opba.restapi.shared.HttpHeaders.COMPUTE_PSU_IP_ADDRESS;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_IP_ADDRESS;

@UtilityClass
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor", "PMD.AvoidUsingHardCodedIP"}) //Checkstyle doesn't recognise Lombok
public class AisStagesCommonUtil {

    public static final String GET_CONSENT_AUTH_STATE = "/v1/consent/{serviceSessionId}";
    public static final String DENY_CONSENT_AUTH_ENDPOINT = "/v1/consent/{serviceSessionId}/deny";
    public static final String AUTHORIZE_CONSENT_ENDPOINT = "/v1/consent/{serviceSessionId}/embedded";
    public static final String AIS_ACCOUNTS_ENDPOINT = "/v1/banking/ais/accounts";
    public static final String AIS_TRANSACTIONS_ENDPOINT = "/v1/banking/ais/accounts/{resourceId}/transactions";

    public static final String REGISTER_USER_ENDPOINT = "/v1/psu/register";
    public static final String AIS_LOGIN_USER_ENDPOINT = "/v1/psu/ais/{authorizationId}/for-approval/login";
    public static final String CONFIRM_CONSENT_ENDPOINT = "/v1/banking/consents/{authorizationId}/confirm";

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";

    public static final String DEFAULT_FINTECH_ID = "MY-SUPER-FINTECH-ID";
    public static final String SANDBOX_BANK_ID = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
    public static final String FINTECH_REDIR_OK = "http://localhost:5500/fintech-callback/ok";
    public static final String FINTECH_REDIR_NOK = "http://localhost:5500/fintech-callback/nok";
    public static final String SESSION_PASSWORD = "qwerty";
    public static final String ANTON_BRUECKNER = "anton.brueckner";
    public static final String MAX_MUSTERMAN = "max.musterman";
    public static final String COMPUTE_IP_ADDRESS = "false";
    public static final String IP_ADDRESS = "1.1.1.1";
    public static final String DEFAULT_X_REQUEST_ID = "3ab706f2-8cc8-462e-8393-a43f6ee87e53";
    public static final String DEFAULT_X_TIMESTAMP_UTC = "2020-04-17T13:45:17.069Z";
    public static final String DEFAULT_X_REQUEST_SIGNATURE = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJmaW50ZWNoQGF3ZXNvbWUtZmludGVjaC5jb20iLCJpc3MiOiJmaW50Z"
                                                                     + "WNoLmNvbSIsInNpZ24tZGF0YSI6IjNhYjcwNmYyLThjYzgtNDYyZS04MzkzLWE0M2Y2ZWU4N2U1M"
                                                                     + "zIwMjAtMDQtMTdUMTM6NDU6MTcuMDY5WiJ9.S3L4XdAhlzJBXYHTXMXVNlLmABBkUvYqF03znEmzKQU"
                                                                     + "9vOF-n0cT6yWWjvm6T82ISzZ5OYrJaA2QJekFsw78vraY-t7vxhWVn9hO_C1tJR_rV3SFWi6mtZeuSCGD"
                                                                     + "SJxEB_8gmMqFomQs0sEdBayiC1mkW9R3TQGhmLkXyM4GHGR_rHL1oLFjG3Ueo0tYmLVIJDyQ6oqFHhDdNr"
                                                                     + "o41O2E1S9BOOVLbANLU7r_jN8KIuujmFIBF3S7L0P2yvIHQ3Sme3W2550m-LdPI3f2SFD4ZRLG6Xsc8Lyr"
                                                                     + "DuXtEuk9H3nHqPenbhQnMPHK7OUcsEN2VFqvUQ9SWTgUz4P9nuU2ng";

    public static RequestSpecification withDefaultHeaders(String fintechUserId) {
        return withSignedHeadersWithoutIpAddress(fintechUserId)
                .header(COMPUTE_PSU_IP_ADDRESS, COMPUTE_IP_ADDRESS)
                .header(PSU_IP_ADDRESS, IP_ADDRESS);
    }

    public static RequestSpecification withSignedHeadersWithoutIpAddress(String fintechUserId) {
        RequestSpecification specification = RestAssured
                                                     .given()
                                                     .header(BANK_ID, SANDBOX_BANK_ID)
                                                     .header(FINTECH_REDIRECT_URL_OK, FINTECH_REDIR_OK)
                                                     .header(FINTECH_REDIRECT_URL_NOK, FINTECH_REDIR_NOK)
                                                     .header(FINTECH_USER_ID, fintechUserId)
                                                     .header(SERVICE_SESSION_PASSWORD, SESSION_PASSWORD)
                                                     .header(X_REQUEST_ID, DEFAULT_X_REQUEST_ID)
                                                     .header(X_TIMESTAMP_UTC, DEFAULT_X_TIMESTAMP_UTC)
                                                     .header(X_REQUEST_SIGNATURE, DEFAULT_X_REQUEST_SIGNATURE);

        return withSignatureHeaders(specification);
    }

    public static RequestSpecification withSignatureHeaders(RequestSpecification specification) {
        return specification
                       .header(FINTECH_ID, DEFAULT_FINTECH_ID)
                       .header(X_REQUEST_ID, DEFAULT_X_REQUEST_ID)
                       .header(X_TIMESTAMP_UTC, DEFAULT_X_TIMESTAMP_UTC)
                       .header(X_REQUEST_SIGNATURE, DEFAULT_X_REQUEST_SIGNATURE);

    }
}

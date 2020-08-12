package de.adorsys.fintech.tests.e2e.steps;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import java.util.UUID;

public class FintechStagesUtils {

    public static final String X_REQUEST_ID = "X-REQUEST-ID";
    public static final String PIN = "1234";
    public static String OBA_USERNAME = "htc";
    public static String OBA_USERNAME_IBAN = "DE59737352520000000108";
    public static final String X_XSRF_TOKEN = "xsrfToken";
    public static final String SESSION_COOKIE = "sessionCookie";
    public static final String ACCOUNT = "/account";
    public static final String PAYMENT = "/accounts";
    public static final String BANKSEARCH_LOGIN = "/login";
    public static final String KEYWORD = "adorsys xs2a";
    public static final String ACCOUNT_ENDPOINT = "https://obg-dev-fintechserver.cloud.adorsys.de/v1/ais/banks/{bank-id}/accounts";
    public static final String FINTECH_SERVER_LOGIN = "https://obg-dev-fintechserver.cloud.adorsys.de/v1/login";
    public static final String FINTECH_UI_URI = "https://obg-dev-fintechui.cloud.adorsys.de";
    public static final String REDIRECT_MODE = "adorsys redirect";
    public static final String EMBEDDED_MODE = "adorsys embedded";


    public static RequestSpecification withDefaultHeaders() {
        return RestAssured
                       .given()
                       .header(X_REQUEST_ID, UUID.randomUUID().toString())
                       .header(X_XSRF_TOKEN, UUID.randomUUID().toString());
         }
}

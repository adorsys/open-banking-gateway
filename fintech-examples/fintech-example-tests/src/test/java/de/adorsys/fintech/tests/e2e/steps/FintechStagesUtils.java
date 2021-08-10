package de.adorsys.fintech.tests.e2e.steps;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import java.util.UUID;

public class FintechStagesUtils {

    public static final String X_REQUEST_ID = "X-REQUEST-ID";
    public static final String PIN = "12345";
    public static final String X_XSRF_TOKEN = "xsrfToken";
    public static final String SESSION_COOKIE = "sessionCookie";
    public static final String ACCOUNT = "/account";
    public static final String TRANSACTION = "/accounts";
    public static final String BANKSEARCH_LOGIN = "/login";
    public static final String ADORSYS_XS2A = "adorsys xs2a";
    public static final String REDIRECT_MODE = "redirect";
    public static final String EMBEDDED_MODE = "embedded";


    public static RequestSpecification withDefaultHeaders() {
        return RestAssured
                       .given()
                       .header(X_REQUEST_ID, UUID.randomUUID().toString())
                       .header(X_XSRF_TOKEN, UUID.randomUUID().toString());
         }
}

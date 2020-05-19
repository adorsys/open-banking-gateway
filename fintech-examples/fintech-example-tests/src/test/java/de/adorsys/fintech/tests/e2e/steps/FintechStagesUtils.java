package de.adorsys.fintech.tests.e2e.steps;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import java.util.UUID;

public class FintechStagesUtils {

    public static final String X_REQUEST_ID = "X-REQUEST-ID";
    public static final String PIN = "1234";
    public static String USERNAME = "bob";
    public static String FINTECH_LOGIN = "tom";
    public static final String X_XSRF_TOKEN = "xsrfToken";
    public static final String SESSION_COOKIE = "sessionCookie";
    public static final String ACCOUNT = "/account/";
    public static final String BANKPROFILE_ENDPOINT = "https://obg-dev-fintechserver.cloud.adorsys.de/v1/search/bankProfile?bankId=";
    public static final String BANKSEARCH_ENDPOINT = "https://obg-dev-fintechserver.cloud.adorsys.de/v1/search/bankSearch";
    public static final String BANKSEARCH_LOGIN = "/login";
    public static final String KEYWORD = "adorsys xs2a";
    public static final String ACCOUNT_ENDPOINT = "https://obg-dev-fintechserver.cloud.adorsys.de/v1/ais/banks/{bank-id}/accounts";
    public static final String FINTECH_SERVER_LOGIN = "https://obg-dev-fintechserver.cloud.adorsys.de/v1/login";
    public static final String FINTECH_UI_URI = "https://obg-dev-fintechui.cloud.adorsys.de";

    public static RequestSpecification withDefaultHeaders() {
        return RestAssured
                       .given()
                       .header(X_REQUEST_ID, UUID.randomUUID().toString())
                       .header(X_XSRF_TOKEN, UUID.randomUUID().toString());
         }
}

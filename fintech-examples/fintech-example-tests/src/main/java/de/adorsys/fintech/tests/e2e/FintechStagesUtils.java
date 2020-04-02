package de.adorsys.fintech.tests.e2e;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import java.util.UUID;

public class FintechStagesUtils {

    public static final String X_REQUEST_ID = "X_REQUEST_ID";
    public static final String PIN_VALUE = "1234";
    public static final String USERNAME = "tom";
    public static final String X_XSRF_TOKEN = "X_XSRF_TOKEN";
    public static final String X_XSRF_TOKEN_VALUE = "441c1f6f-1bda-43c0-91d2-4b41a5d53a9c";
    public static final String SESSION_COOKIE = "SESSION_COOKIE";
    public static final String SESSION_COOKIE_VALUE = "%7B%22fintechUserId%22%3A%226164%22%2C%22hashedXsrfToken%22%3A1713814359%7D";
    public static final String BANK_ID = "BANK_ID";
    public static final String BANK_ID_VALUE = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
    public static final String FINTECH_LOGIN_ENDPOINT = "/v1/login";
    public static final String BANKSEARCH = "adorsys xs2a";
    public static final String BANKSEARCH_ENDPOINT = "/v1/search/bankSearch";


    public static RequestSpecification withDefaultHeaders() {
        return RestAssured.given().header(X_REQUEST_ID, UUID.randomUUID().toString());
    }
}

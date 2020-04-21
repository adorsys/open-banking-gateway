package de.adorsys.fintech.tests.e2e.steps;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import java.util.UUID;

public class FintechStagesUtils {

    public static final String X_REQUEST_ID = "X_REQUEST_ID";
    public static final String PIN = "1234";
    public static String USERNAME = "bob";
    public static final String X_XSRF_TOKEN = "xsrfToken";
    public static final String SESSION_COOKIE = "sessionCookie";
    public static final String BANK_ID_VALUE = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
    public static final String ACCOUNT = "account/";
    public static final String BANKPROFILE_ENDPOINT = "/bank/";
    public static final String BANKSEARCH_LOGIN = "/login";
    public static final String BANKSEARCH = "/search";

    public static RequestSpecification withDefaultHeaders() {
        return RestAssured.given().header(X_REQUEST_ID, UUID.randomUUID().toString());
    }
}

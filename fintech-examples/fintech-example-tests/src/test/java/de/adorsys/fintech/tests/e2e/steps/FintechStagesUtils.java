package de.adorsys.fintech.tests.e2e.steps;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import java.util.UUID;

public class FintechStagesUtils {

    public static final String X_REQUEST_ID = "X_REQUEST_ID";
    public static final String PIN = "1234";
    public static  String USERNAME = getRandomUser() + "ffd";
    public static final String X_XSRF_TOKEN = "xsrfToken";
    public static final String X_XSRF_TOKEN_VALUE = "441c1f6f-1bda-43c0-91d2-4b41a5d53a9c";
    public static final String SESSION_COOKIE = "sessionCookie";
    public static final String SESSION_COOKIE_VALUE = "%7B%22fintechUserId%22%3A%226164%22%2C%22hashedXsrfToken%22%3A1713814359%7D";
    public static final String BANK_ID = "BANK_ID";
    public static final String BANK_ID_VALUE = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
    public static final String FINTECH_LOGIN_ENDPOINT = "login";
    public static final String ACCOUNT = "account/";
    public static final String ANTON_BRUECKNER_ID = "cmD4EYZeTkkhxRuIV1diKA";
    public static final String BANKSEARCH = "adorsys xs2a";
    public static final String BANKPROFILE_ENDPOINT = "https://obg-dev-fintechui.cloud.adorsys.de/bank/";
    public static final String BANKSEARCH_ENDPOINT = "https://obg-dev-fintechui.cloud.adorsys.de/search";


    private static String getRandomUser() {
        String names[] = {"anton", "thierry", "tomy", "ttit"};
        int id = (int) ((Math.random() * (names.length - 1)) + 1);;
        return names[id];
    }

    public static RequestSpecification withDefaultHeaders() {
        return RestAssured.given().header(X_REQUEST_ID, UUID.randomUUID().toString());
    }
}

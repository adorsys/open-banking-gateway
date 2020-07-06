package de.adorsys.opba.smoketests.steps;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.SandboxServers;
import de.adorsys.opba.smoketests.config.SmokeConfig;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static de.adorsys.opba.protocol.xs2a.tests.e2e.ResourceUtil.readResource;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.TPP_MANAGEMENT_AUTH_HEADER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.TPP_MANAGEMENT_AUTH_TOKEN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.TPP_MANAGEMENT_CREATE_ACCOUNT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.TPP_MANAGEMENT_CREATE_USER_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.TPP_MANAGEMENT_DEPOSIT_CASH_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.TPP_MANAGEMENT_GET_ACCOUNT_DETAILS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.TPP_MANAGEMENT_IBAN_QUERY;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.TPP_MANAGEMENT_LOGIN_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.TPP_MANAGEMENT_LOGIN_HEADER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.TPP_MANAGEMENT_PASSWORD_HEADER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.TPP_MANAGEMENT_USER_ID_QUERY;

@Slf4j
@JGivenStage
public class SmokeSandboxServers<SELF extends SmokeSandboxServers<SELF>> extends SandboxServers<SELF> {

    private static final String USER_NAME_PLACEHOLDER = "%login%";
    private static final String PASSWORD_PLACEHOLDER = "%password%";
    private static final String EMAIL_PLACEHOLDER = "%email%";
    private static final String IBAN_PLACEHOLDER = "%iban%";

    @Autowired
    private SmokeConfig config;

    @ProvidedScenarioState
    protected String iban;

    @ProvidedScenarioState
    protected String accountResourceId;


    @SneakyThrows
    public SELF create_new_user_in_sandbox_tpp_management(String login, String password) {
        String auth = login_into_tpp_management();
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        String userId = do_create_new_user_in_sandbox_tpp_management(login, password, auth);
        create_account_for_user_in_sandbox_tpp_management(auth, userId);
        String accountId = get_account_id_by_iban_in_sandbox_tpp_management(auth, iban);

        return deposit_to_sandbox_tpp_management_user_account(auth, accountId);
    }

    private String login_into_tpp_management() {
        ExtractableResponse<Response> response = RestAssured
                 .given()
                     .header(TPP_MANAGEMENT_LOGIN_HEADER, config.getSandboxTppManagementUserName())
                     .header(TPP_MANAGEMENT_PASSWORD_HEADER, config.getSandboxTppManagementPassword())
                 .when()
                    .post(config.getSandboxTppManagementServerUrl() + TPP_MANAGEMENT_LOGIN_ENDPOINT)
                 .then()
                     .statusCode(HttpStatus.OK.value())
                     .extract();

        return "Bearer " + response.header(TPP_MANAGEMENT_AUTH_TOKEN);
    }

    @SneakyThrows
    private String do_create_new_user_in_sandbox_tpp_management(String login, String password, String auth) {
        String body = readResource("restrecord/tpp-ui-input/params/new-user-registration-body.json")
                              .replace(USER_NAME_PLACEHOLDER, login)
                              .replace(PASSWORD_PLACEHOLDER, password)
                              .replace(EMAIL_PLACEHOLDER, login + "@example.com");

        ExtractableResponse<Response> response = RestAssured
                 .given()
                     .contentType(MediaType.APPLICATION_JSON_VALUE)
                     .header(TPP_MANAGEMENT_AUTH_HEADER, auth)
                     .body(body)
                 .when()
                    .post(config.getSandboxTppManagementServerUrl() + TPP_MANAGEMENT_CREATE_USER_ENDPOINT)
                 .then()
                     .statusCode(HttpStatus.OK.value())
                     .extract();

        return response.body().jsonPath().getString("id");
    }

    private SELF create_account_for_user_in_sandbox_tpp_management(String auth, String userId) {
        this.iban = Iban.random(CountryCode.DE).toString();
        String body = readResource("restrecord/tpp-ui-input/params/new-user-new-account-registration.json")
                              .replace(IBAN_PLACEHOLDER, iban);

        RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(TPP_MANAGEMENT_AUTH_HEADER, auth)
                    .queryParam(TPP_MANAGEMENT_USER_ID_QUERY, userId)
                    .body(body)
                .when()
                    .post(config.getSandboxTppManagementServerUrl() + TPP_MANAGEMENT_CREATE_ACCOUNT_ENDPOINT)
                .then()
                    .statusCode(HttpStatus.OK.value());

        return self();
    }

    @SneakyThrows
    private String get_account_id_by_iban_in_sandbox_tpp_management(String auth, String iban) {
        ExtractableResponse<Response> response = RestAssured
                 .given()
                     .contentType(MediaType.APPLICATION_JSON_VALUE)
                     .header(TPP_MANAGEMENT_AUTH_HEADER, auth)
                     .queryParam(TPP_MANAGEMENT_IBAN_QUERY, iban)
                 .when()
                    .get(config.getSandboxTppManagementServerUrl() + TPP_MANAGEMENT_GET_ACCOUNT_DETAILS_ENDPOINT)
                 .then()
                     .statusCode(HttpStatus.OK.value())
                     .extract();

        this.accountResourceId = response.body().jsonPath().getString("id");
        return accountResourceId;
    }

    private SELF deposit_to_sandbox_tpp_management_user_account(String auth, String accountId) {
        String body = readResource("restrecord/tpp-ui-input/params/new-user-deposit-account.json");

        RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(TPP_MANAGEMENT_AUTH_HEADER, auth)
                    .body(body)
                .when()
                    .post(config.getSandboxTppManagementServerUrl() + TPP_MANAGEMENT_DEPOSIT_CASH_ENDPOINT, accountId)
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value());

        return self();
    }
}

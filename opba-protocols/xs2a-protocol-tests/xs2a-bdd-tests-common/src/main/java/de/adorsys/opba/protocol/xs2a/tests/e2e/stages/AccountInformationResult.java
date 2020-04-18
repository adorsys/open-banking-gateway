package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.SERVICE_SESSION_PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.ResourceUtil.readResource;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationRequestCommon.REDIRECT_CODE_QUERY;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.AIS_ACCOUNTS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.AIS_TRANSACTIONS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.ANTON_BRUECKNER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.AUTHORIZE_CONSENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.CONFIRM_CONSENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.MAX_MUSTERMAN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.SESSION_PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.withDefaultHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.BigDecimalComparator.BIG_DECIMAL_COMPARATOR;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.http.HttpHeaders.LOCATION;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class AccountInformationResult extends Stage<AccountInformationResult>  {

    private static final int ANTON_BRUECKNER_BOOKED_TRANSACTIONS_COUNT = 8;
    private static final int MAX_MUSTERMAN_BOOKED_TRANSACTIONS_COUNT = 5;
    private static final String ANTON_BRUECKNER_IBAN = "DE80760700240271232400";
    private static final String MAX_MUSTERMAN_IBAN = "DE38760700240320465700";

    @Getter
    @ExpectedScenarioState
    private String responseContent;

    @ExpectedScenarioState
    protected String serviceSessionId;

    @ExpectedScenarioState
    protected String authSessionCookie;

    @Autowired
    private ConsentRepository consents;

    @ProvidedScenarioState
    protected String redirectCode;

    @SneakyThrows
    @Transactional
    public AccountInformationResult open_banking_has_consent_for_anton_brueckner_account_list() {
        assertThat(consents.findByServiceSessionId(UUID.fromString(serviceSessionId))).isNotEmpty();
        return self();
    }

    @SneakyThrows
    @Transactional
    public AccountInformationResult open_banking_has_no_consent() {
        assertThat(consents.findByServiceSessionId(UUID.fromString(serviceSessionId))).isEmpty();
        return self();
    }

    @Transactional
    public AccountInformationResult open_banking_has_consent_for_max_musterman_account_list() {
        assertThat(consents.findByServiceSessionId(UUID.fromString(serviceSessionId))).isNotEmpty();
        return self();
    }

    @SneakyThrows
    @Transactional
    public AccountInformationResult open_banking_has_consent_for_anton_brueckner_transaction_list() {
        assertThat(consents.findByServiceSessionId(UUID.fromString(serviceSessionId))).isNotEmpty();
        return self();
    }

    @SneakyThrows
    @Transactional
    public AccountInformationResult open_banking_has_consent_for_max_musterman_transaction_list() {
        assertThat(consents.findByServiceSessionId(UUID.fromString(serviceSessionId))).isNotEmpty();
        return self();
    }

    @SneakyThrows
    public AccountInformationResult open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session() {
        return open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session(true);
    }

    @SneakyThrows
    public AccountInformationResult open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session(
        boolean validateResourceId
    ) {
        ExtractableResponse<Response> response = withDefaultHeaders(ANTON_BRUECKNER)
                    .header(SERVICE_SESSION_ID, serviceSessionId)
                .when()
                    .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("accounts[0].iban", equalTo(ANTON_BRUECKNER_IBAN))
                    .body("accounts[0].resourceId", validateResourceId ? equalTo("cmD4EYZeTkkhxRuIV1diKA") : instanceOf(String.class))
                    .body("accounts[0].currency", equalTo("EUR"))
                    .body("accounts[0].name", equalTo("anton.brueckner"))
                    .body("accounts", hasSize(1))
                .extract();

        this.responseContent = response.body().asString();
        return self();
    }

    @SneakyThrows
    public AccountInformationResult open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session() {
        return open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(true);
    }

    @SneakyThrows
    public AccountInformationResult open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(
        boolean validateResourceId
    ) {
        ExtractableResponse<Response> response = withDefaultHeaders(ANTON_BRUECKNER)
                    .header(SERVICE_SESSION_ID, serviceSessionId)
                .when()
                    .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("accounts[0].iban", equalTo(MAX_MUSTERMAN_IBAN))
                    .body("accounts[0].resourceId", validateResourceId ? equalTo("oN7KTVuJSVotMvPPPavhVo") : instanceOf(String.class))
                    .body("accounts[0].currency", equalTo("EUR"))
                    .body("accounts[0].name", equalTo("max.musterman"))
                    .body("accounts", hasSize(1))
                    .extract();

        this.responseContent = response.body().asString();
        return self();
    }

    @SneakyThrows
    public AccountInformationResult open_banking_reads_anton_brueckner_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
        String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        ExtractableResponse<Response> response = getTransactionListFor(ANTON_BRUECKNER, resourceId, dateFrom, dateTo, bookingStatus);

        this.responseContent = response.body().asString();
        DocumentContext body = JsonPath.parse(responseContent);

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].creditorAccount.iban")).asList()
                .containsOnly(
                        "DE67760700240243265400",
                        "DE23760700240234367800",
                        "DE80760700240271232400",
                        "DE84100100100568753108",
                        "DE38760700240320465700"
                );

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].debtorAccount.iban")).asList()
                .containsOnly("DE80760700240271232400", "DE38760700240320465700");

        assertThat(body)
                .extracting(it -> it.read("$.transactions.booked[*].transactionAmount.amount"))
                .asList()
                .extracting(it -> new BigDecimal((String) it))
                .usingElementComparator(BIG_DECIMAL_COMPARATOR)
                // Looks like returned order by Sandbox is not stable
                .containsOnly(
                        new BigDecimal("-150.00"),
                        new BigDecimal("-100.00"),
                        new BigDecimal("-2300.00"),
                        new BigDecimal("-250.00"),
                        new BigDecimal("2300.00"),
                        new BigDecimal("-900.00"),
                        new BigDecimal("-700.00"),
                        new BigDecimal("30000.00")
                );
        return self();
    }

    private ExtractableResponse<Response> getTransactionListFor(
        String psuId, String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        return withDefaultHeaders(psuId)
                .header(SERVICE_SESSION_ID, serviceSessionId)
                .queryParam("dateFrom", dateFrom.format(ISO_DATE))
                .queryParam("dateTo", dateTo.format(ISO_DATE))
                .queryParam("bookingStatus", bookingStatus)
            .when()
                .get(AIS_TRANSACTIONS_ENDPOINT, resourceId)
            .then()
                .statusCode(HttpStatus.OK.value())
            .extract();
    }

    public AccountInformationResult open_banking_can_read_anton_brueckner_transactions_data_using_consent_bound_to_service_session(
        String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        withDefaultHeaders(ANTON_BRUECKNER)
                .header(SERVICE_SESSION_ID, serviceSessionId)
                .queryParam("dateFrom", dateFrom.format(ISO_DATE))
                .queryParam("dateTo", dateTo.format(ISO_DATE))
                .queryParam("bookingStatus", bookingStatus)
            .when()
                .get(AIS_TRANSACTIONS_ENDPOINT, resourceId)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("transactions.booked.transactionId",
                    containsInAnyOrder(
                        "rnvGvu2TR2Yl99bAoM_skY",
                        "1Lag4mgPRy4kLuz1rRifJ4",
                        "xKVwpTr9TaAoW9j1Zem4Tw",
                        "GrrnMdDgTGIjM-w_kkTVSA",
                        "mfSdvTvYThwr8hocMJMsxA",
                        "Tt7Os27bTc0vC6jDk0f5lY",
                        "qlI0mwopQIknL0n-U4bD80",
                        "pG7GZlccRPsoBNudHnX25Q"
                    )
                )
                .body("transactions.booked", hasSize(ANTON_BRUECKNER_BOOKED_TRANSACTIONS_COUNT));
        return self();
    }

    @SneakyThrows
    public AccountInformationResult open_banking_can_read_max_musterman_transactions_data_using_consent_bound_to_service_session(
        String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        withDefaultHeaders(MAX_MUSTERMAN)
                .header(SERVICE_SESSION_ID, serviceSessionId)
                .queryParam("dateFrom", dateFrom.format(ISO_DATE))
                .queryParam("dateTo", dateTo.format(ISO_DATE))
                .queryParam("bookingStatus", bookingStatus)
            .when()
                .get(AIS_TRANSACTIONS_ENDPOINT, resourceId)
            .then()
                .statusCode(HttpStatus.OK.value())
            .body("transactions.booked.transactionId",
                containsInAnyOrder(
                    "VHF5-8R1RCcskezln6CJAY",
                    "etA9KGhIT9ohX9dYXrhzc8",
                    "LjwVWzBBQtwpyQ6WBBTiwk",
                    "pkOyTAHDTb0uCF2R55HKKo",
                    "F3qVhSXlQswswIN2nk1rBo"
                )
            )
            .body("transactions.booked", hasSize(MAX_MUSTERMAN_BOOKED_TRANSACTIONS_COUNT));
        return self();
    }

    @SneakyThrows
    public AccountInformationResult open_banking_reads_max_musterman_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
        String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        ExtractableResponse<Response> response = getTransactionListFor(MAX_MUSTERMAN, resourceId, dateFrom, dateTo, bookingStatus);

        this.responseContent = response.body().asString();
        DocumentContext body = JsonPath.parse(responseContent);

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].creditorAccount.iban")).asList()
                .containsOnly(
                        "DE69760700240340283600",
                        "DE80760700240271232400",
                        "DE38760700240320465700"
                );

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].debtorAccount.iban")).asList()
                .containsOnly("DE38760700240320465700", "DE80760700240271232400", "DE69760700240340283600");

        assertThat(body)
                .extracting(it -> it.read("$.transactions.booked[*].transactionAmount.amount"))
                .asList()
                .extracting(it -> new BigDecimal((String) it))
                .usingElementComparator(BIG_DECIMAL_COMPARATOR)
                // Looks like returned order by Sandbox is not stable
                .containsOnly(
                        new BigDecimal("-1280.00"),
                        new BigDecimal("-2300.00"),
                        new BigDecimal("700.00"),
                        new BigDecimal("3000.00"),
                        new BigDecimal("10000.00")
                );
        return self();
    }

    public AccountInformationResult user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent_and_gets_202() {
        String body = readResource("restrecord/tpp-ui-input/params/anton-brueckner-account-all-accounts-consent.json");

        ExtractableResponse<Response> response = RestAssured
                .given()
                    .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .queryParam(REDIRECT_CODE_QUERY, redirectCode)
                    .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                .when()
                    .post(AUTHORIZE_CONSENT_ENDPOINT, serviceSessionId)
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                .extract();

        assertThat(response.header(LOCATION)).matches(".+/ais/.+");
        return self();
    }

    public AccountInformationResult fintech_calls_consent_activation_for_current_authorization_id() {
        RestAssured
                .given()
                    .header(SERVICE_SESSION_PASSWORD, SESSION_PASSWORD)
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post(CONFIRM_CONSENT_ENDPOINT, serviceSessionId)
                .then()
                    .statusCode(HttpStatus.OK.value());
        return self();
    }
}

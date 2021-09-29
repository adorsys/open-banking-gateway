package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.protocol.xs2a.tests.e2e.LocationExtractorUtil;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.SERVICE_SESSION_PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.ResourceUtil.readResource;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.RequestCommon.X_XSRF_TOKEN_QUERY;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AIS_ACCOUNTS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AIS_TRANSACTIONS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.ANTON_BRUECKNER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AUTHORIZE_CONSENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.CONFIRM_CONSENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.MAX_MUSTERMAN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.SANDBOX_BANK_PROFILE_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.SESSION_PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.VOLKSBANK_BANK_PROFILE_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withAccountsHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withSignatureHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withTransactionsHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.BigDecimalComparator.BIG_DECIMAL_COMPARATOR;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class AccountInformationResult<SELF extends AccountInformationResult<SELF>> extends Stage<SELF> {

    private static final int ANTON_BRUECKNER_BOOKED_TRANSACTIONS_COUNT = 8;
    private static final int MAX_MUSTERMAN_BOOKED_TRANSACTIONS_COUNT = 5;
    private static final String ANTON_BRUECKNER_IBAN = "DE80760700240271232400";
    private static final String MAX_MUSTERMAN_IBAN = "DE38760700240320465700";
    public static final String ONLINE = "online";

    @Getter
    @ExpectedScenarioState
    protected String responseContent;

    @ExpectedScenarioState
    protected String serviceSessionId;

    @ExpectedScenarioState
    protected String authSessionCookie;

    @ExpectedScenarioState
    protected String iban;

    @ExpectedScenarioState
    protected String accountResourceId;

    @Autowired
    protected ConsentRepository consents;

    @ProvidedScenarioState
    protected String redirectCode;

    @Autowired
    protected RequestSigningService requestSigningService;

    @SneakyThrows
    @Transactional
    public SELF open_banking_has_consent_for_anton_brueckner_account_list() {
        assertThat(consents.findByServiceSessionIdOrderByModifiedAtDesc(UUID.fromString(serviceSessionId))).isNotEmpty();
        return self();
    }

    @SneakyThrows
    @Transactional
    public SELF open_banking_has_no_consent_for_anton_brueckner_account_list() {
        assertThat(consents.findByServiceSessionIdOrderByModifiedAtDesc(UUID.fromString(serviceSessionId))).isEmpty();
        return self();
    }

    @SneakyThrows
    @Transactional
    public SELF open_banking_has_no_consent() {
        assertThat(consents.findByServiceSessionIdOrderByModifiedAtDesc(UUID.fromString(serviceSessionId))).isEmpty();
        return self();
    }

    @Transactional
    public SELF open_banking_has_consent_for_max_musterman_account_list() {
        assertThat(consents.findByServiceSessionIdOrderByModifiedAtDesc(UUID.fromString(serviceSessionId))).isNotEmpty();
        return self();
    }

    @SneakyThrows
    @Transactional
    public SELF open_banking_has_consent_for_anton_brueckner_transaction_list() {
        assertThat(consents.findByServiceSessionIdOrderByModifiedAtDesc(UUID.fromString(serviceSessionId))).isNotEmpty();
        return self();
    }

    @SneakyThrows
    @Transactional
    public SELF open_banking_has_consent_for_max_musterman_transaction_list() {
        assertThat(consents.findByServiceSessionIdOrderByModifiedAtDesc(UUID.fromString(serviceSessionId))).isNotEmpty();
        return self();
    }

    @SneakyThrows
    public SELF open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session() {
        return open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session(true);
    }

    @SneakyThrows
    public SELF open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session(String bankProfileId) {
        return open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session(true, bankProfileId);
    }

    @SneakyThrows
    public SELF admin_check_that_bank_is_deleted(String bankUuid) {
        AdminUtil.adminChecksThatBankIsDeleted(bankUuid);
        return self();
    }

    @SneakyThrows
    public SELF open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session(
            boolean validateResourceId
    ) {
        open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session_and_bank_profile_id(validateResourceId, SANDBOX_BANK_PROFILE_ID);
        return self();
    }

    @SneakyThrows
    public SELF open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session(
            boolean validateResourceId, String bankProfileId
    ) {
        open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session_and_bank_profile_id(validateResourceId, bankProfileId);
        return self();
    }

    private void open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session_and_bank_profile_id(boolean validateResourceId, String bankProfileId) {
        ExtractableResponse<Response> response = withAccountsHeaders(ANTON_BRUECKNER, bankProfileId)
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
    }

    public void open_banking_can_not_read_anton_brueckner_account_data_using_consent_bound_to_service_session_and_bank_profile_id() {
                withAccountsHeaders(ANTON_BRUECKNER, SANDBOX_BANK_PROFILE_ID)
                     .header(SERVICE_SESSION_ID, serviceSessionId)
                .when()
                     .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                     .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                     .body("message", equalTo("No bank profile for bank: " + SANDBOX_BANK_PROFILE_ID))
                .extract();
    }

    @SneakyThrows
    public SELF open_banking_can_read_user_account_data_using_consent_bound_to_service_session(
            String user, boolean validateResourceId
    ) {
        ExtractableResponse<Response> response = withAccountsHeaders(user)
                     .header(SERVICE_SESSION_ID, serviceSessionId)
                .when()
                     .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                     .statusCode(HttpStatus.OK.value())
                     .body("accounts[0].iban", equalTo(iban))
                     .body("accounts[0].resourceId", validateResourceId ? equalTo(accountResourceId) : instanceOf(String.class))
                     .body("accounts[0].currency", equalTo("EUR"))
                     .body("accounts[0].name", equalTo(user))
                     .body("accounts", hasSize(1))
                .extract();

        this.responseContent = response.body().asString();
        return self();
    }

    @SneakyThrows
    public SELF open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session() {
        return open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(true);
    }

    @SneakyThrows
    public SELF open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(String bankProfileId) {
        return open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(true, 0, true, bankProfileId);
    }

    @SneakyThrows
    public SELF open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(
            boolean validateResourceId
    ) {
        return open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(validateResourceId, 0);
    }

    @SneakyThrows
    public SELF open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(
            boolean validateResourceId, int expectedBalances
    ) {
        return open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(validateResourceId, expectedBalances, true, SANDBOX_BANK_PROFILE_ID);
    }

    @SneakyThrows
    public SELF open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session(
            boolean validateResourceId, int expectedBalances, boolean online, String bankProfileId
    ) {
        ValidatableResponse body = withAccountsHeaders(ANTON_BRUECKNER, bankProfileId)
                     .header(SERVICE_SESSION_ID, serviceSessionId)
                .when()
                     .queryParam("online", online)
                     .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("accounts[0].iban", equalTo(MAX_MUSTERMAN_IBAN))
                    .body("accounts[0].resourceId", validateResourceId ? equalTo("oN7KTVuJSVotMvPPPavhVo") : instanceOf(String.class))
                    .body("accounts[0].currency", equalTo("EUR"))
                    .body("accounts[0].name", equalTo("max.musterman"))
                    .body("accounts", hasSize(1));
        if (expectedBalances > 0) {
            body.body("accounts[0].balances", hasSize(expectedBalances));
        }
        ExtractableResponse<Response> response = body.extract();
        this.responseContent = response.body().asString();
        return self();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @SneakyThrows
    public SELF open_banking_can_read_volksbank_account_data_using_consent_bound_to_service_session(boolean online) {
        ValidatableResponse body = withAccountsHeaders(ANTON_BRUECKNER, VOLKSBANK_BANK_PROFILE_ID)
            .header(SERVICE_SESSION_ID, serviceSessionId)
            .when()
            .queryParam("online", online)
            .queryParam("withBalance", false)
            .get(AIS_ACCOUNTS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("accounts[0].iban", equalTo(MAX_MUSTERMAN_IBAN))
            .body("accounts[0].resourceId", equalTo("oN7KTVuJSVotMvPPPavhVo"))
            .body("accounts[0].currency", equalTo("EUR"))
            .body("accounts[0].ownerName", equalTo("max.musterman"))
            .body("accounts", hasSize(15));
        ExtractableResponse<Response> response = body.extract();
        this.responseContent = response.body().asString();
        return self();
    }


    @SneakyThrows
    public SELF open_banking_reads_anton_brueckner_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
            String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        ExtractableResponse<Response> response = getTransactionListFor(ANTON_BRUECKNER, resourceId, dateFrom, dateTo, bookingStatus);

        this.responseContent = response.body().asString();
        DocumentContext body = JsonPath.parse(responseContent);

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].creditorAccount.iban")).asList()
                .containsOnly(
                        "DE80760700240271232400", "DE80760700240271232400", "DE80760700240271232400",
                        "DE80760700240271232400", "DE80760700240271232400", "DE80760700240271232400",
                        "DE80760700240271232400", "DE80760700240271232400"
                );

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].debtorAccount.iban")).asList()
                .containsOnly(
                        "DE23760700240234367800", "DE67760700240243265400", "DE38760700240320465700",
                        "DE84100100100568753108", "DE38760700240320465700", "DE80760700240271232400"
                );

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

    @SneakyThrows
    public SELF open_banking_reads_user_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
            String user, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        ExtractableResponse<Response> response = getTransactionListFor(user, accountResourceId, dateFrom, dateTo, bookingStatus);

        this.responseContent = response.body().asString();
        DocumentContext body = JsonPath.parse(responseContent);

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].creditorAccount.iban")).asList()
                .containsOnly(iban);

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].debtorAccount.iban")).asList()
                .containsOnly(iban);

        assertThat(body)
                .extracting(it -> it.read("$.transactions.booked[*].transactionAmount.amount"))
                .asList()
                .extracting(it -> new BigDecimal((String) it))
                .usingElementComparator(BIG_DECIMAL_COMPARATOR)
                // Looks like returned order by Sandbox is not stable
                .containsOnly(
                        new BigDecimal("1000.00")
                );
        return self();
    }

    protected ExtractableResponse<Response> getTransactionListFor(String psuId, String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus) {
        return getTransactionListFor(psuId, SANDBOX_BANK_PROFILE_ID, resourceId, dateFrom, dateTo, bookingStatus);
    }

    protected ExtractableResponse<Response> getTransactionListFor(
            String psuId, String bankProfileId, String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        return withTransactionsHeaders(psuId, bankProfileId)
                    .header(SERVICE_SESSION_ID, serviceSessionId)
                    .queryParam("dateFrom", dateFrom.format(ISO_DATE))
                    .queryParam("dateTo", dateTo.format(ISO_DATE))
                    .queryParam("bookingStatus", bookingStatus)
                    .queryParam(ONLINE, false)
                .when()
                    .get(AIS_TRANSACTIONS_ENDPOINT, resourceId)
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract();
    }

    public SELF open_banking_can_read_anton_brueckner_transactions_data_using_consent_bound_to_service_session(
            String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        withTransactionsHeaders(ANTON_BRUECKNER)
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
    public SELF open_banking_can_read_max_musterman_transactions_data_using_consent_bound_to_service_session(
            String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        return open_banking_can_read_max_musterman_transactions_data_using_consent_bound_to_service_session(
                resourceId, dateFrom, dateTo, bookingStatus, true
        );
    }

    @SneakyThrows
    public SELF open_banking_can_read_max_musterman_transactions_data_using_consent_bound_to_service_session(
            String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus, boolean online
    ) {
        withTransactionsHeaders(MAX_MUSTERMAN)
                    .header(SERVICE_SESSION_ID, serviceSessionId)
                    .queryParam("dateFrom", dateFrom.format(ISO_DATE))
                    .queryParam("dateTo", dateTo.format(ISO_DATE))
                    .queryParam("bookingStatus", bookingStatus)
                    .queryParam("online", online)
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
    public SELF open_banking_can_read_max_musterman_transactions_data_using_consent_bound_to_service_session_volksbank(
        String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus, boolean online
    ) {
        withTransactionsHeaders(MAX_MUSTERMAN)
            .header(SERVICE_SESSION_ID, serviceSessionId)
            .queryParam("dateFrom", dateFrom.format(ISO_DATE))
            .queryParam("dateTo", dateTo.format(ISO_DATE))
            .queryParam("bookingStatus", bookingStatus)
            .queryParam("online", online)
            .when()
            .get(AIS_TRANSACTIONS_ENDPOINT, resourceId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("transactions.booked.mandateId", equalTo(Collections.singletonList("VHF5-8R1RCcskezln6CJAY")))
            .body("transactions.booked", hasSize(1));
        return self();
    }

    @SneakyThrows
    public SELF open_banking_can_read_none_due_to_filter_max_musterman_transactions_data_using_consent_bound_to_service_session(
            String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus, boolean online
    ) {
        withTransactionsHeaders(MAX_MUSTERMAN)
                    .header(SERVICE_SESSION_ID, serviceSessionId)
                    .queryParam("dateFrom", dateFrom.format(ISO_DATE))
                    .queryParam("dateTo", dateTo.format(ISO_DATE))
                    .queryParam("bookingStatus", bookingStatus)
                    .queryParam("online", online)
                .when()
                    .get(AIS_TRANSACTIONS_ENDPOINT, resourceId)
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("transactions.booked", empty());
        return self();
    }

    @SneakyThrows
    public SELF open_banking_reads_max_musterman_transactions_using_consent_bound_to_service_session_data_validated_by_iban(
            String resourceId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        ExtractableResponse<Response> response = getTransactionListFor(MAX_MUSTERMAN, resourceId, dateFrom, dateTo, bookingStatus);

        this.responseContent = response.body().asString();
        DocumentContext body = JsonPath.parse(responseContent);

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].creditorAccount.iban")).asList()
                .containsOnly(
                        "DE38760700240320465700",
                        "DE38760700240320465700",
                        "DE38760700240320465700",
                        "DE38760700240320465700",
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

    public SELF user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent_and_gets_202() {
        String body = readResource("restrecord/tpp-ui-input/params/anton-brueckner-account-all-accounts-consent.json");

        ExtractableResponse<Response> response = RestAssured
                .given()
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                    .queryParam(X_XSRF_TOKEN_QUERY, redirectCode)
                    .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                .when()
                    .post(AUTHORIZE_CONSENT_ENDPOINT, serviceSessionId)
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .extract();

        assertThat(LocationExtractorUtil.getLocation(response)).matches(".+/ais/.+");
        return self();
    }

    public SELF fintech_calls_consent_activation_for_current_authorization_id(String serviceSessionId, HttpStatus status) {
        withSignatureHeaders(RestAssured
                .given()
                    .header(SERVICE_SESSION_PASSWORD, SESSION_PASSWORD)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
                .when()
                    .post(CONFIRM_CONSENT_ENDPOINT, serviceSessionId)
                .then()
                    .statusCode(status.value());
        return self();
    }

    public SELF fintech_calls_consent_activation_for_current_authorization_id() {
        return fintech_calls_consent_activation_for_current_authorization_id(serviceSessionId, HttpStatus.OK);
    }

    public SELF fintech_calls_consent_activation_for_current_authorization_id_failed_with_not_found() {
        return fintech_calls_consent_activation_for_current_authorization_id(serviceSessionId, HttpStatus.NOT_FOUND);
    }

    @SneakyThrows
    public SELF open_banking_can_read_max_musterman_hbci_account_data_using_consent_bound_to_service_session(String bankProfileId) {
        ExtractableResponse<Response> response = withAccountsHeaders(MAX_MUSTERMAN, bankProfileId)
                    .header(SERVICE_SESSION_ID, serviceSessionId)
                    .queryParam(ONLINE, false)
                .when()
                    .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("accounts[0].iban", equalTo("DE59300000033466865655"))
                    .body("accounts[0].resourceId", instanceOf(String.class))
                    .body("accounts[0].currency", equalTo("EUR"))
                    .body("accounts[0].name", equalTo("Extra-Konto"))
                    .body("accounts[1].iban", equalTo("DE13300000032278292697"))
                    .body("accounts[1].resourceId", instanceOf(String.class))
                    .body("accounts[1].currency", equalTo("EUR"))
                    .body("accounts[1].name", equalTo("Extra-Konto"))
                    .body("accounts", hasSize(2))
                .extract();

        this.responseContent = response.body().asString();
        return self();
    }

    @SneakyThrows
    public SELF open_banking_can_read_anton_brueckner_hbci_account_data_using_consent_bound_to_service_session(String bankProfileId) {
        ExtractableResponse<Response> response = withAccountsHeaders(ANTON_BRUECKNER, bankProfileId)
                    .header(SERVICE_SESSION_ID, serviceSessionId)
                    .queryParam(ONLINE, false)
                .when()
                    .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("accounts[0].iban", equalTo("DE65300000035827542519"))
                    .body("accounts[0].resourceId", instanceOf(String.class))
                    .body("accounts[0].currency", equalTo("EUR"))
                    .body("accounts[0].name", equalTo("Extra-Konto"))
                    .body("accounts[1].iban", equalTo("DE17300000039185286653"))
                    .body("accounts[1].resourceId", instanceOf(String.class))
                    .body("accounts[1].currency", equalTo("EUR"))
                    .body("accounts[1].name", equalTo("Extra-Konto"))
                    .body("accounts", hasSize(2))
                .extract();

        this.responseContent = response.body().asString();
        return self();
    }

    @SneakyThrows
    public SELF open_banking_can_read_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session(
            String resourceId, String bankProfileId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        ExtractableResponse<Response> response = getTransactionListFor(MAX_MUSTERMAN, bankProfileId, resourceId, dateFrom, dateTo, bookingStatus);

        this.responseContent = response.body().asString();
        DocumentContext body = JsonPath.parse(responseContent);

        // TODO: Currently no IBANs as mapping is not yet completed
        assertThat(body).extracting(it -> it.read("$.transactions.booked[*]")).asList().hasSize(1);
        assertThat(body)
                .extracting(it -> it.read("$.transactions.booked[*].transactionAmount.amount"))
                .asList()
                .extracting(it -> new BigDecimal((String) it))
                .usingElementComparator(BIG_DECIMAL_COMPARATOR)
                // Looks like returned order by Sandbox is not stable
                .containsOnly(
                        new BigDecimal("-100.00")
                );
        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].remittanceInformationUnstructured"))
                .asList().containsOnly("Payment For Account Insurance");
        return self();
    }

    @SneakyThrows
    public SELF open_banking_can_read_empty_due_to_range_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session(
            String resourceId, String bankProfileId, LocalDate dateFrom, LocalDate dateTo, String bookingStatus
    ) {
        ExtractableResponse<Response> response = getTransactionListFor(MAX_MUSTERMAN, bankProfileId, resourceId, dateFrom, dateTo, bookingStatus);

        this.responseContent = response.body().asString();
        DocumentContext body = JsonPath.parse(responseContent);

        // TODO: Currently no IBANs as mapping is not yet completed
        assertThat(body).extracting(it -> it.read("$.transactions.booked[*]")).asList().hasSize(0);
        return self();
    }

    public SELF fintech_calls_authorization_session_state(String expectedSessionState, String expectedAuthSessionState) {
        RequestStatusUtil.fintechCallsAisAuthorizationSessionState(expectedSessionState, expectedAuthSessionState, serviceSessionId);
        return self();
    }
}

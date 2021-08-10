package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.db.repository.jpa.PaymentRepository;
import de.adorsys.xs2a.adapter.api.model.PaymentProduct;
import de.adorsys.xs2a.adapter.api.model.TransactionStatus;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.SERVICE_SESSION_PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.PIS_ANONYMOUS_LOGIN_USER_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.withPaymentInfoHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.RequestCommon.REDIRECT_CODE_QUERY;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.CONFIRM_PAYMENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.PIS_PAYMENT_INFORMATION_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.PIS_PAYMENT_STATUS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.SANDBOX_BANK_PROFILE_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.SESSION_PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withSignatureHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class PaymentResult<SELF extends PaymentResult<SELF>> extends Stage<SELF> {

    @Autowired
    private PaymentRepository payments;

    @ExpectedScenarioState
    protected String paymentServiceSessionId;

    @ExpectedScenarioState
    protected String redirectUriToGetUserParams;

    @Transactional
    public SELF open_banking_has_stored_payment() {
        assertThat(payments.findByServiceSessionIdOrderByModifiedAtDesc(UUID.fromString(paymentServiceSessionId))).isNotEmpty();
        return self();
    }

    public SELF user_logged_in_into_opba_as_anonymous_user_with_credentials_using_fintech_supplied_url_is_forbidden() {
        String fintechUserTempPassword = UriComponentsBuilder
                .fromHttpUrl(redirectUriToGetUserParams).build()
                .getQueryParams()
                .getFirst(REDIRECT_CODE_QUERY);

        RestAssured
            .given()
                .header(X_REQUEST_ID, UUID.randomUUID().toString())
                .queryParam(REDIRECT_CODE_QUERY, fintechUserTempPassword)
                .contentType(APPLICATION_JSON_VALUE)
            .when()
                .post(PIS_ANONYMOUS_LOGIN_USER_ENDPOINT, paymentServiceSessionId)
            .then()
                .statusCode(BAD_REQUEST.value());
        return self();
    }

    public SELF fintech_calls_payment_activation_for_current_authorization_id() {
        fintech_calls_payment_activation_for_current_authorization_id(paymentServiceSessionId);
        return self();
    }

    public SELF fintech_calls_payment_information_iban_400() {
        return fintech_calls_payment_information("DE80760700240271232400");
    }

    public SELF fintech_calls_payment_information_iban_700() {
        return fintech_calls_payment_information("DE38760700240320465700");
    }

    public SELF fintech_calls_payment_information(String iban) {
        ExtractableResponse<Response> response = withPaymentInfoHeaders(UUID.randomUUID().toString())
                .header(SERVICE_SESSION_ID, paymentServiceSessionId)
            .when()
                .get(PIS_PAYMENT_INFORMATION_ENDPOINT, PaymentProduct.SEPA_CREDIT_TRANSFERS.toString())
            .then()
                .statusCode(OK.value())
                .body("endToEndIdentification", equalTo("WBG-123456789"))
                .body("debtorAccount.iban", equalTo(iban))
                .body("debtorAccount.currency", equalTo("EUR"))
                .body("instructedAmount.currency", equalTo("EUR"))
                .body("instructedAmount.amount", equalTo("1.03"))
                .body("creditorAccount.iban", equalTo(iban))
                .body("creditorAccount.currency", equalTo("EUR"))
                .body("creditorAgent", equalTo("AAAADEBBXXX"))
                .body("creditorName", equalTo("WBG"))
                .body("creditorAddress.streetName", equalTo("WBG Straße"))
                .body("creditorAddress.buildingNumber", equalTo("56"))
                .body("creditorAddress.postCode", equalTo("90543"))
                .body("creditorAddress.townName", equalTo("Nürnberg"))
                .body("creditorAddress.country", equalTo("DE"))
                .body("remittanceInformationUnstructured", equalTo("Ref. Number WBG-1222"))
                .body("transactionStatus", equalTo(TransactionStatus.ACSP.name()))
                .extract();

        assertThat(ZonedDateTime.now(ZoneOffset.UTC)).isAfterOrEqualTo(ZonedDateTime.parse(response.body().jsonPath().getString("createdAt")));
        return self();
    }

    public SELF fintech_calls_payment_information_iban_400_wiremock() {
        return fintech_calls_payment_information_wiremock("DE80760700240271232400");
    }

    public SELF fintech_calls_payment_information_iban_700_wiremock() {
        return fintech_calls_payment_information_wiremock("DE38760700240320465700");
    }

    public SELF fintech_calls_payment_information_wiremock(String iban) {
        ExtractableResponse<Response> response = withPaymentInfoHeaders(UUID.randomUUID().toString())
                .header(SERVICE_SESSION_ID, paymentServiceSessionId)
            .when()
                .get(PIS_PAYMENT_INFORMATION_ENDPOINT, PaymentProduct.SEPA_CREDIT_TRANSFERS.toString())
            .then()
                .statusCode(OK.value())
                .body("endToEndIdentification", emptyOrNullString())
                .body("debtorAccount.iban", equalTo(iban))
                .body("debtorAccount.currency", equalTo("EUR"))
                .body("instructedAmount.currency", equalTo("EUR"))
                .body("instructedAmount.amount", equalTo("12.34"))
                .body("creditorAccount.iban", equalTo("AL90208110080000001039531801"))
                .body("creditorAccount.currency", equalTo("EUR"))
                .body("creditorAgent", emptyOrNullString())
                .body("creditorName", equalTo("peter"))
                .body("creditorAddress.streetName", emptyOrNullString())
                .body("creditorAddress.buildingNumber", emptyOrNullString())
                .body("creditorAddress.postCode", emptyOrNullString())
                .body("creditorAddress.country", emptyOrNullString())
                .body("remittanceInformationUnstructured", equalTo("test transfer"))
                .body("transactionStatus", equalTo(TransactionStatus.ACSC.name()))
                .extract();

        assertThat(ZonedDateTime.now(ZoneOffset.UTC)).isAfterOrEqualTo(ZonedDateTime.parse(response.body().jsonPath().getString("createdAt")));
        return self();
    }

    public SELF fintech_calls_payment_information_hbci(String iban, String bankProfileId, String expectedStatus) {
        ExtractableResponse<Response> response = withPaymentInfoHeaders(UUID.randomUUID().toString(), bankProfileId)
                .header(SERVICE_SESSION_ID, paymentServiceSessionId)
            .when()
                .get(PIS_PAYMENT_INFORMATION_ENDPOINT, PaymentProduct.SEPA_CREDIT_TRANSFERS.toString())
            .then()
                .statusCode(OK.value())
                .body("endToEndIdentification", equalTo("WBG-123456789"))
                .body("debtorAccount.iban", equalTo(iban))
                .body("debtorAccount.currency", equalTo("EUR"))
                .body("instructedAmount.currency", equalTo("EUR"))
                .body("instructedAmount.amount", equalTo("1.03"))
                .body("creditorAccount.iban", equalTo("DE38760700240320465700"))
                .body("creditorAccount.currency", equalTo("EUR"))
                .body("creditorAgent", equalTo("AAAADEBBXXX"))
                .body("creditorName", equalTo("WBG"))
                .body("creditorAddress.streetName", equalTo("WBG Straße"))
                .body("creditorAddress.buildingNumber", equalTo("56"))
                .body("creditorAddress.townName", equalTo("Nürnberg"))
                .body("creditorAddress.postCode", equalTo("90543"))
                .body("creditorAddress.country", equalTo("DE"))
                .body("remittanceInformationUnstructured", equalTo("Ref. Number WBG-1222"))
                .body("transactionStatus", equalTo(expectedStatus))
                .extract();

        assertThat(ZonedDateTime.now(ZoneOffset.UTC)).isAfterOrEqualTo(ZonedDateTime.parse(response.body().jsonPath().getString("createdAt")));
        return self();
    }

    public SELF fintech_calls_payment_status() {
        return fintech_calls_payment_status(SANDBOX_BANK_PROFILE_ID, TransactionStatus.ACSP.name());
    }

    public SELF fintech_calls_payment_status(String bankProfileId, String expectedStatus, String serviceSessionId) {
        ExtractableResponse<Response> response = withPaymentInfoHeaders(UUID.randomUUID().toString(), bankProfileId)
                .header(SERVICE_SESSION_ID, serviceSessionId)
            .when()
                .get(PIS_PAYMENT_STATUS_ENDPOINT, PaymentProduct.SEPA_CREDIT_TRANSFERS.toString())
            .then()
                .statusCode(OK.value())
                .body("transactionStatus", equalTo(expectedStatus))
                .extract();
        assertThat(ZonedDateTime.now(ZoneOffset.UTC)).isAfterOrEqualTo(ZonedDateTime.parse(response.body().jsonPath().getString("createdAt")));
        return self();
    }

    public SELF fintech_calls_payment_status(String bankProfileId, String expectedStatus) {
        return fintech_calls_payment_status(bankProfileId, expectedStatus, paymentServiceSessionId);
    }

    public SELF fintech_calls_payment_activation_for_current_authorization_id(String serviceSessionId) {
        withSignatureHeaders(RestAssured
            .given()
                .header(SERVICE_SESSION_PASSWORD, SESSION_PASSWORD)
                .contentType(APPLICATION_JSON_VALUE))
            .when()
                .post(CONFIRM_PAYMENT_ENDPOINT, serviceSessionId)
            .then()
                .statusCode(HttpStatus.OK.value());
        return self();
    }
}


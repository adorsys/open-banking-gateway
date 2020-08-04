package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.db.repository.jpa.PaymentRepository;
import de.adorsys.xs2a.adapter.adapter.StandardPaymentProduct;
import de.adorsys.xs2a.adapter.service.model.TransactionStatus;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.SERVICE_SESSION_PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.PIS_ANONYMOUS_LOGIN_USER_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.withPaymentInfoHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.RequestCommon.REDIRECT_CODE_QUERY;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.CONFIRM_PAYMENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.PIS_PAYMENT_INFORMATION_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.PIS_PAYMENT_STATUS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.SANDBOX_BANK_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.SESSION_PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withSignatureHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class PaymentResult<SELF extends PaymentResult<SELF>> extends Stage<SELF> {

    @Autowired
    private PaymentRepository payments;

    @Autowired
    private RequestSigningService requestSigningService;

    @ExpectedScenarioState
    protected String serviceSessionId;

    @ExpectedScenarioState
    protected String redirectUriToGetUserParams;

    @Transactional
    public SELF open_banking_has_stored_payment() {
        assertThat(payments.findByServiceSessionIdOrderByModifiedAtDesc(UUID.fromString(serviceSessionId))).isNotEmpty();
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
                    .post(PIS_ANONYMOUS_LOGIN_USER_ENDPOINT, serviceSessionId)
                .then()
                    .statusCode(BAD_REQUEST.value());
        return self();
    }

    public SELF fintech_calls_payment_activation_for_current_authorization_id() {
        fintech_calls_payment_activation_for_current_authorization_id(serviceSessionId);
        return self();
    }

    public void fintech_calls_payment_information_iban_400() {
        fintech_calls_payment_information("DE80760700240271232400");
    }

    public void fintech_calls_payment_information_iban_700() {
        fintech_calls_payment_information("DE38760700240320465700");
    }

    public SELF fintech_calls_payment_information(String iban) {
        withPaymentInfoHeaders("", requestSigningService, OperationType.PIS)
                .header(SERVICE_SESSION_ID, serviceSessionId)
            .when()
                .get(PIS_PAYMENT_INFORMATION_ENDPOINT, StandardPaymentProduct.SEPA_CREDIT_TRANSFERS.getSlug())
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
                .body("creditorAddress.country", equalTo("DE"))
                .body("remittanceInformationUnstructured", equalTo("Ref. Number WBG-1222"))
                .body("transactionStatus", equalTo(TransactionStatus.ACSP.name()))
                .extract();
        return self();
    }

    public SELF fintech_calls_payment_status() {
        return fintech_calls_payment_status(SANDBOX_BANK_ID, TransactionStatus.ACSP.name());
    }

    public SELF fintech_calls_payment_status(String bankId, String expectedStatus) {
        withPaymentInfoHeaders("", requestSigningService, OperationType.PIS, bankId)
                .header(SERVICE_SESSION_ID, serviceSessionId)
            .when()
                .get(PIS_PAYMENT_STATUS_ENDPOINT, StandardPaymentProduct.SEPA_CREDIT_TRANSFERS.getSlug())
            .then()
                .statusCode(OK.value())
                .body("transactionStatus", equalTo(expectedStatus))
                .extract();
        return self();
    }

    public SELF fintech_calls_payment_activation_for_current_authorization_id(String serviceSessionId) {
        withSignatureHeaders(RestAssured
                                     .given()
                                     .header(SERVICE_SESSION_PASSWORD, SESSION_PASSWORD)
                                     .contentType(APPLICATION_JSON_VALUE), requestSigningService, OperationType.CONFIRM_PAYMENT)
                .when()
                    .post(CONFIRM_PAYMENT_ENDPOINT, serviceSessionId)
                .then()
                    .statusCode(HttpStatus.OK.value());
        return self();
    }
}


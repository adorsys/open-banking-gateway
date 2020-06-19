package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.xs2a.adapter.adapter.StandardPaymentProduct;
import de.adorsys.xs2a.adapter.service.model.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.PIS_PAYMENT_INFORMATION_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.PIS_PAYMENT_STATUS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.withPaymentInfoHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.OK;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class PaymentResult<SELF extends PaymentResult<SELF>> extends Stage<SELF> {
    @Autowired
    private ConsentRepository consents;

    @Autowired
    private RequestSigningService requestSigningService;

    @Autowired
    private AccountInformationResult accountInformationResult;

    @ExpectedScenarioState
    protected String serviceSessionId;


    @Transactional
    public SELF open_banking_has_consent_for_max_musterman_payment() {
        assertThat(consents.findByServiceSessionId(UUID.fromString(serviceSessionId))).isNotEmpty();
        return self();
    }

    @Transactional
    public SELF open_banking_has_consent_for_anton_brueckner_payment() {
        assertThat(consents.findByServiceSessionId(UUID.fromString(serviceSessionId))).isNotEmpty();
        return self();
    }

    public SELF fintech_calls_consent_activation_for_current_authorization_id() {
        accountInformationResult.fintech_calls_consent_activation_for_current_authorization_id(serviceSessionId);
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
        withPaymentInfoHeaders("", requestSigningService, OperationType.PIS)
                .header(SERVICE_SESSION_ID, serviceSessionId)
            .when()
                .get(PIS_PAYMENT_STATUS_ENDPOINT, StandardPaymentProduct.SEPA_CREDIT_TRANSFERS.getSlug())
            .then()
                .statusCode(OK.value())
                .body("transactionStatus", equalTo(TransactionStatus.ACSP.name()))
                .extract();
        return self();
    }
}


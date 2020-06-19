package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.xs2a.adapter.adapter.StandardPaymentProduct;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.SERVICE_SESSION_PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.CONFIRM_CONSENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.PIS_PAYMENT_INFORMATION_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.PIS_PAYMENT_STATUS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.withSignatureHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.withPaymentInfoHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class PaymentResult<SELF extends PaymentResult<SELF>> extends Stage<SELF> {
    @Autowired
    protected ConsentRepository consents;

    @Autowired
    protected RequestSigningService requestSigningService;

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
        withSignatureHeaders(RestAssured
                .given()
                .header(SERVICE_SESSION_PASSWORD, AisStagesCommonUtil.SESSION_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON_VALUE), requestSigningService, OperationType.CONFIRM_CONSENT)
                .when()
                .post(CONFIRM_CONSENT_ENDPOINT, serviceSessionId)
                .then()
                .statusCode(HttpStatus.OK.value());
        return self();
    }

    public SELF fintech_calls_payment_information() {
        withPaymentInfoHeaders("", requestSigningService, OperationType.PIS)
                .header(SERVICE_SESSION_ID, serviceSessionId)
            .when()
                .get(PIS_PAYMENT_INFORMATION_ENDPOINT, StandardPaymentProduct.SEPA_CREDIT_TRANSFERS.getSlug())
            .then()
                .statusCode(OK.value())
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
                .extract();
        return self();
    }
}


package de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentRequestCommon;
import de.adorsys.xs2a.adapter.adapter.StandardPaymentProduct;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.BANK_BLZ_30000003_ID;
import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.ResourceUtil.readResource;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.withPaymentHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AUTHORIZE_CONSENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.MAX_MUSTERMAN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.PIS_SINGLE_PAYMENT_ENDPOINT;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@JGivenStage
public class HbciPaymentInitiationRequest<SELF extends HbciPaymentInitiationRequest<SELF>> extends PaymentRequestCommon<SELF> {

    public SELF fintech_calls_single_payment_for_max_musterman_for_blz_30000003() {
        return fintech_calls_single_payment_for_max_musterman(MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID, BANK_BLZ_30000003_ID);
    }

    public SELF fintech_calls_single_payment_for_max_musterman(String resourceId, String bankId) {
        return fintech_calls_single_payment_for_max_musterman(resourceId, bankId, "Ref. Number WBG-1222");
    }

    public SELF fintech_calls_single_payment_for_max_musterman(String resourceId, String bankId, String remittance) {
        String body = readResource("restrecord-input-params/hbci-max-musterman-single-sepa-payment.json");
        body = body.replaceAll("%debtorIban%", resourceId).replaceAll("%remittance%", remittance);
        ExtractableResponse<Response> response = withPaymentHeaders(MAX_MUSTERMAN, bankId)
                .contentType(APPLICATION_JSON_VALUE)
                .body(body)
            .when()
                .post(PIS_SINGLE_PAYMENT_ENDPOINT, StandardPaymentProduct.SEPA_CREDIT_TRANSFERS.getSlug())
            .then()
                .statusCode(ACCEPTED.value())
                .extract();

        updateServiceSessionId(response);
        updateRedirectCode(response);
        updateNextPaymentAuthorizationUrl(response);
        return self();
    }

    public SELF user_max_musterman_selected_sca_challenge_type_push_tan_to_embedded_authorization() {
        provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
                selectedScaBody("pushTAN"),
                ACCEPTED
        );
        return self();
    }
}

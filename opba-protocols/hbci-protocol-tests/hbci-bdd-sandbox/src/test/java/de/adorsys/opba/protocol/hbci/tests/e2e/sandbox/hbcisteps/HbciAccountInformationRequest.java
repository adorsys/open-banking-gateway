package de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationRequestCommon;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.BANK_BLZ_20000002_ID;
import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.BANK_BLZ_30000003_ID;
import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.MAX_MUSTERMAN_BANK_BLZ_20000002_ACCOUNT_ID;
import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.ResourceUtil.readResource;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AIS_TRANSACTIONS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AUTHORIZE_CONSENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.MAX_MUSTERMAN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withTransactionsHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.ACCEPTED;

@JGivenStage
public class HbciAccountInformationRequest<SELF extends HbciAccountInformationRequest<SELF>> extends AccountInformationRequestCommon<SELF> {

    public SELF fintech_calls_list_accounts_for_max_musterman_for_blz_30000003() {
        return fintech_calls_list_accounts_for_max_musterman_forBank(BANK_BLZ_30000003_ID);
    }

    public SELF fintech_calls_list_accounts_for_anton_brueckner_for_blz_30000003() {
        return fintech_calls_list_accounts_for_anton_brueckner(BANK_BLZ_30000003_ID);
    }

    public SELF fintech_calls_list_accounts_max_musterman_for_blz_20000002() {
        return fintech_calls_list_accounts_for_max_musterman_forBank(BANK_BLZ_20000002_ID);
    }

    public SELF fintech_calls_list_transactions_for_max_musterman_for_blz_30000003() {
        return fintech_calls_list_transactions_for_max_musterman(MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID, BANK_BLZ_30000003_ID);
    }

    public SELF fintech_calls_list_transactions_for_max_musterman_for_blz_20000002() {
        return fintech_calls_list_transactions_for_max_musterman(MAX_MUSTERMAN_BANK_BLZ_20000002_ACCOUNT_ID, BANK_BLZ_20000002_ID);
    }

    public SELF fintech_calls_list_transactions_for_max_musterman_using_already_granted_service_session(String resourceId, String bankId) {
        ExtractableResponse<Response> response = withTransactionsHeaders(MAX_MUSTERMAN, bankId)
                    .header(SERVICE_SESSION_ID, serviceSessionId)
                .when()
                    .get(AIS_TRANSACTIONS_ENDPOINT, resourceId)
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                .extract();

        updateServiceSessionId(response);
        updateRedirectCode(response);
        updateNextConsentAuthorizationUrl(response);
        return self();
    }


    public SELF user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/anton-brueckner-account-all-accounts-consent.json")
        );

        return self();
    }

    public SELF user_anton_brueckner_provided_correct_pin_to_embedded_authorization_and_sees_redirect_to_fintech_ok() {
        ExtractableResponse<Response> response = anton_brueckner_provides_password();
        assertThat(response.header(LOCATION)).contains("ais").contains("consent-result");
        return self();
    }

    public SELF user_max_musterman_provided_correct_pin_to_embedded_authorization_and_sees_redirect_to_fintech_ok() {
        ExtractableResponse<Response> response = max_musterman_provides_password();
        assertThat(response.header(LOCATION)).contains("ais").contains("consent-result");
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

    protected ExtractableResponse<Response> anton_brueckner_provides_password() {
        return startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/anton-brueckner-password.json")
        );
    }
}

package de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationRequestCommon;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.BANK_BLZ_20000002_ID;
import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.BANK_BLZ_30000003_ID;
import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.MAX_MUSTERMAN_BANK_BLZ_20000002_ACCOUNT_ID;
import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AUTHORIZE_CONSENT_ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.ACCEPTED;

@JGivenStage
public class HbciAccountInformationRequest<SELF extends HbciAccountInformationRequest<SELF>> extends AccountInformationRequestCommon<SELF> {

    public SELF fintech_calls_list_accounts_for_max_musterman_for_blz_30000003() {
        return fintech_calls_list_accounts_for_max_musterman(BANK_BLZ_30000003_ID);
    }

    public SELF fintech_calls_list_accounts_for_anton_brueckner_for_blz_30000003() {
        return fintech_calls_list_accounts_for_anton_brueckner(BANK_BLZ_30000003_ID);
    }

    public SELF fintech_calls_list_transactions_for_max_musterman_for_blz_30000003() {
        return fintech_calls_list_transactions_for_max_musterman(MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID, BANK_BLZ_30000003_ID);
    }

    public SELF fintech_calls_list_transactions_for_max_musterman_for_blz_20000002() {
        return fintech_calls_list_transactions_for_max_musterman(MAX_MUSTERMAN_BANK_BLZ_20000002_ACCOUNT_ID, BANK_BLZ_20000002_ID);
    }

    public SELF user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                "restrecord/tpp-ui-input/params/anton-brueckner-account-all-accounts-consent.json"
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
                "restrecord/tpp-ui-input/params/anton-brueckner-password.json"
        );
    }
}

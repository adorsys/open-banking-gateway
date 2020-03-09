package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.stress;

import com.tngtech.jgiven.junit5.DualScenarioTest;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockAccountInformationRequest;
import org.junit.jupiter.api.Test;

import static de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockConst.BOTH_BOOKING;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockConst.DATE_FROM;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockConst.DATE_TO;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockConst.MAX_MUSTERMAN_RESOURCE_ID;

/**
 * This is not really a test, just steps supplier.
 */
class E2EStress extends DualScenarioTest<WiremockAccountInformationRequest<? extends WiremockAccountInformationRequest<?>>, AccountInformationResult> {

    @Test
    void embeddedAccountList() {

        when()
            .fintech_calls_list_accounts_for_max_musterman()
            .and()
            .user_max_musterman_provided_initial_parameters_to_list_accounts_all_accounts_consent()
            .and()
            .user_max_musterman_provided_password_to_embedded_authorization()
            .and()
            .user_max_musterman_selected_sca_challenge_type_email2_to_embedded_authorization()
            .and()
            .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_redirect_to_fintech_ok();

        then()
            .open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session();
    }

    @Test
    void embeddedTransactionList() {

        when()
            .fintech_calls_list_transactions_for_max_musterman()
            .and()
            .user_max_musterman_provided_initial_parameters_to_list_transactions_with_single_account_consent()
            .and()
            .user_max_musterman_provided_password_to_embedded_authorization()
            .and()
            .user_max_musterman_selected_sca_challenge_type_email1_to_embedded_authorization()
            .and()
            .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_redirect_to_fintech_ok();
        then()
            .open_banking_can_read_max_musterman_transactions_data_using_consent_bound_to_service_session(
                MAX_MUSTERMAN_RESOURCE_ID, DATE_FROM, DATE_TO, BOTH_BOOKING
            );
    }
}

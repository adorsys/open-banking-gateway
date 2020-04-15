package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.MockServers;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockAccountInformationRequest;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockConst;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.Xs2aProtocolApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.MOCKED_SANDBOX;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * User consent authorization abortion test that uses wiremock-stubbed request-responses to drive banking-protocol.
 */
/*
As we redefine list accounts for adorsys-sandbox bank to sandbox customary one
(and it doesn't make sense to import sandbox module here as it is XS2A test) moving it back to plain xs2a bean:
 */
@Sql(statements = "UPDATE opb_bank_protocol SET protocol_bean_name = 'xs2aListTransactions' WHERE protocol_bean_name = 'xs2aSandboxListTransactions'")

@SuppressWarnings("CPD-START") // Same steps are used, but thats fine for readability
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {Xs2aProtocolApplication.class, JGivenConfig.class}, webEnvironment = RANDOM_PORT)
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS, MOCKED_SANDBOX})
class WiremockE2EXs2aProtocolAuthorizationDenyTest extends SpringScenarioTest<MockServers, WiremockAccountInformationRequest<? extends WiremockAccountInformationRequest<?>>, AccountInformationResult> {

    @LocalServerPort
    private int port;

    @Autowired
    private ProtocolConfiguration configuration;

    // See https://github.com/spring-projects/spring-boot/issues/14879 for the 'why setting port'
    @BeforeEach
    void setBaseUrl() {
        ProtocolConfiguration.Redirect.Consent consent = configuration.getRedirect().getConsentAccounts();
        consent.setOk(consent.getOk().replaceAll("localhost:\\d+", "localhost:" + port));
        consent.setNok(consent.getNok().replaceAll("localhost:\\d+", "localhost:" + port));
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testAccountsListWithConsentUsingRedirectDenyBeforeConsent(Approach approach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_accounts_running()
                .preferred_sca_approach_selected_for_all_banks_in_opba(approach)
                .rest_assured_points_to_opba_server();

        when()
                .fintech_calls_list_accounts_for_anton_brueckner()
                .and()
                .user_denied_consent();

        then()
                .open_banking_has_no_consent();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testTransactionsListWithConsentUsingRedirectDenyBeforeConsent(Approach approach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_transactions_running()
                .preferred_sca_approach_selected_for_all_banks_in_opba(approach)
                .rest_assured_points_to_opba_server();

        when()
                .fintech_calls_list_transactions_for_anton_brueckner(WiremockConst.ANTON_BRUECKNER_RESOURCE_ID)
                .and()
                .user_denied_consent();

        then()
                .open_banking_has_no_consent();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testAccountsListWithConsentUsingEmbeddedDenyBeforeConsent(Approach approach) {
        given()
                .embedded_mock_of_sandbox_for_max_musterman_accounts_running()
                .preferred_sca_approach_selected_for_all_banks_in_opba(approach)
                .rest_assured_points_to_opba_server();

        when()
                .fintech_calls_list_accounts_for_max_musterman()
                .and()
                .user_denied_consent();

        then()
                .open_banking_has_no_consent();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testTransactionsListWithConsentUsingEmbeddedDenyBeforeConsent(Approach approach) {
        given()
                .embedded_mock_of_sandbox_for_max_musterman_transactions_running()
                .preferred_sca_approach_selected_for_all_banks_in_opba(approach)
                .rest_assured_points_to_opba_server();

        when()
                .fintech_calls_list_transactions_for_max_musterman()
                .and()
                .user_denied_consent();

        then()
                .open_banking_has_no_consent();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testAccountsListWithConsentUsingRedirectDenyAfterConsent(Approach approach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_accounts_running()
                .preferred_sca_approach_selected_for_all_banks_in_opba(approach)
                .rest_assured_points_to_opba_server();

        when()
                .fintech_calls_list_accounts_for_anton_brueckner()
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent()
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
                .and()
                .user_denied_consent();

        then()
                .open_banking_has_no_consent();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testTransactionsListWithConsentUsingRedirectDenyAfterConsent(Approach approach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_transactions_running()
                .preferred_sca_approach_selected_for_all_banks_in_opba(approach)
                .rest_assured_points_to_opba_server();

        when()
                .fintech_calls_list_transactions_for_anton_brueckner(WiremockConst.ANTON_BRUECKNER_RESOURCE_ID)
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_list_transactions_with_single_account_consent()
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
                .and()
                .user_denied_consent();

        then()
                .open_banking_has_no_consent();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testAccountsListWithConsentUsingEmbeddedDenyAfterConsent(Approach approach) {
        given()
                .embedded_mock_of_sandbox_for_max_musterman_accounts_running()
                .preferred_sca_approach_selected_for_all_banks_in_opba(approach)
                .rest_assured_points_to_opba_server();

        when()
                .fintech_calls_list_accounts_for_max_musterman()
                .and()
                .user_max_musterman_provided_initial_parameters_to_list_accounts_all_accounts_consent()
                .and()
                .user_max_musterman_provided_password_to_embedded_authorization()
                .and()
                .user_max_musterman_selected_sca_challenge_type_email2_to_embedded_authorization()
                .and()
                .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok()
                .and()
                .user_denied_consent();

        then()
                .open_banking_has_no_consent();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testTransactionsListWithConsentUsingEmbeddedDenyAfterConsent(Approach approach) {
        given()
                .embedded_mock_of_sandbox_for_max_musterman_transactions_running()
                .preferred_sca_approach_selected_for_all_banks_in_opba(approach)
                .rest_assured_points_to_opba_server();

        when()
                .fintech_calls_list_transactions_for_max_musterman()
                .and()
                .user_max_musterman_provided_initial_parameters_to_list_transactions_with_single_account_consent()
                .and()
                .user_max_musterman_provided_password_to_embedded_authorization()
                .and()
                .user_max_musterman_selected_sca_challenge_type_email1_to_embedded_authorization()
                .and()
                .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok()
                .and()
                .user_denied_consent();

        then()
                .open_banking_has_no_consent();
    }
}

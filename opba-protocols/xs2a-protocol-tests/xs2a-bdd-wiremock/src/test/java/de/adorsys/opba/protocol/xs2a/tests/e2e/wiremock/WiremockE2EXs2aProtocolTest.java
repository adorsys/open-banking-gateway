package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.db.domain.Approach;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.MockServers;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockAccountInformationRequest;
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

import java.time.LocalDate;

import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.MOCKED_SANDBOX;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Happy-path test that uses wiremock-stubbed request-responses to drive banking-protocol.
 */
/*
As we redefine list accounts for adorsys-sandbox bank to sandbox customary one
(and it doesn't make sense to import sandbox module here as it is XS2A test) moving it back to plain xs2a bean:
 */
@Sql(statements = "UPDATE opb_bank_protocol SET protocol_bean_name = 'xs2aListTransactions' WHERE protocol_bean_name = 'xs2aSandboxListTransactions'")

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {Xs2aProtocolApplication.class, JGivenConfig.class}, webEnvironment = RANDOM_PORT)
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS, MOCKED_SANDBOX})
class WiremockE2EXs2aProtocolTest extends SpringScenarioTest<MockServers, WiremockAccountInformationRequest<? extends WiremockAccountInformationRequest<?>>, AccountInformationResult> {

    private static final String ANTON_BRUECKNER_RESOURCE_ID = "cmD4EYZeTkkhxRuIV1diKA";
    private static final String MAX_MUSTERMAN_RESOURCE_ID = "oN7KTVuJSVotMvPPPavhVo";
    private static final LocalDate DATE_FROM = LocalDate.parse("2018-01-01");
    private static final LocalDate DATE_TO = LocalDate.parse("2020-09-30");
    private static final String BOTH_BOOKING = "BOTH";

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
    void testAccountsListWithConsentUsingRedirect(Approach approach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_accounts_running()
                .preferred_sca_approach_selected_for_all_banks_in_opba(approach);

        when()
                .fintech_calls_list_accounts_for_anton_brueckner()
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called();
        then()
                .open_banking_has_consent_for_anton_brueckner_account_list()
                .open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testTransactionsListWithConsentUsingRedirect(Approach approach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_transactions_running()
                .preferred_sca_approach_selected_for_all_banks_in_opba(approach);

        when()
                .fintech_calls_list_transactions_for_anton_brueckner(ANTON_BRUECKNER_RESOURCE_ID)
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_list_transactions_with_single_account_consent()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called();
        then()
                .open_banking_has_consent_for_anton_brueckner_transaction_list()
                .open_banking_can_read_anton_brueckner_transactions_data_using_consent_bound_to_service_session(
                    ANTON_BRUECKNER_RESOURCE_ID, DATE_FROM, DATE_TO, BOTH_BOOKING
                );
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testAccountsListWithConsentUsingEmbedded(Approach approach) {
        given()
                .embedded_mock_of_sandbox_for_max_musterman_accounts_running()
                .preferred_sca_approach_selected_for_all_banks_in_opba(approach);

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
                .open_banking_has_consent_for_max_musterman_account_list()
                .open_banking_can_read_max_musterman_account_data_using_consent_bound_to_service_session();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testTransactionsListWithConsentUsingEmbedded(Approach approach) {
        given()
                .embedded_mock_of_sandbox_for_max_musterman_transactions_running()
                .preferred_sca_approach_selected_for_all_banks_in_opba(approach);

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
                .open_banking_has_consent_for_max_musterman_transaction_list()
                .open_banking_can_read_max_musterman_transactions_data_using_consent_bound_to_service_session(
                    MAX_MUSTERMAN_RESOURCE_ID, DATE_FROM, DATE_TO, BOTH_BOOKING
                );
    }
}

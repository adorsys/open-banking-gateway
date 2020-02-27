package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.MockServers;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockAccountInformationRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @Test
    @SneakyThrows
    void testAccountsListWithConsentUsingRedirect() {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_accounts_running();
        when()
                .open_banking_list_accounts_called_for_anton_brueckner()
                .and()
                .open_banking_user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called();
        then()
                .open_banking_has_consent_for_anton_brueckner_account_list()
                .open_banking_can_read_anton_brueckner_account_data_using_consent_bound_to_service_session();
    }

    @Test
    void testTransactionsListWithConsentUsingRedirect() {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_transactions_running();
        when()
                .open_banking_list_transactions_called_for_anton_brueckner("cmD4EYZeTkkhxRuIV1diKA")
                .and()
                .open_banking_user_anton_brueckner_provided_initial_parameters_to_list_transactions()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called();
        then()
                .open_banking_reads_anton_brueckner_transactions_on_redirect();
    }

    @Test
    void testAccountsListWithConsentUsingEmbedded() {
        given()
                .embedded_mock_of_sandbox_for_max_musterman_accounts_running();
        when()
                .open_banking_list_accounts_called_for_max_musterman()
                .and()
                .open_banking_user_max_musterman_provided_initial_parameters_to_list_accounts()
                .and()
                .open_banking_user_max_musterman_provided_password()
                .and()
                .open_banking_user_max_musterman_selected_sca_challenge_type_email2()
                .and()
                .open_banking_user_max_musterman_provided_sca_challenge_result_and_no_redirect();
        then()
                .open_banking_has_max_musterman_accounts();
    }

    @Test
    void testTransactionsListWithConsentUsingEmbedded() {
        given()
                .embedded_mock_of_sandbox_for_max_musterman_transactions_running();
        when()
                .open_banking_list_transactions_called_for_max_musterman()
                .and()
                .open_banking_user_max_musterman_provided_initial_parameters_to_list_transactions()
                .and()
                .open_banking_user_max_musterman_provided_password()
                .and()
                .open_banking_user_max_musterman_selected_sca_challenge_type_email1()
                .and()
                .open_banking_user_max_musterman_provided_sca_challenge_result_and_no_redirect();
        then()
                .open_banking_has_max_musterman_transactions();
    }
}

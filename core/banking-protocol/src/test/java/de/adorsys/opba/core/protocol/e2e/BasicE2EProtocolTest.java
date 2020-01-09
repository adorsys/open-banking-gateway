package de.adorsys.opba.core.protocol.e2e;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.core.protocol.BankingProtocol;
import de.adorsys.opba.core.protocol.e2e.stages.AccountListRequest;
import de.adorsys.opba.core.protocol.e2e.stages.AccountListResult;
import de.adorsys.opba.core.protocol.e2e.stages.mocks.MockServers;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static de.adorsys.opba.core.protocol.TestProfiles.MOCKED_SANDBOX;
import static de.adorsys.opba.core.protocol.TestProfiles.ONE_TIME_POSTGRES_RAMFS;

/**
 * Happy-path test that uses wiremock-stubbed request-responses to drive banking-protocol.
 */
@AutoConfigureMockMvc
@SpringBootTest(classes = {BankingProtocol.class, JGivenConfig.class})
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS, MOCKED_SANDBOX})
class BasicE2EProtocolTest extends SpringScenarioTest<MockServers, AccountListRequest, AccountListResult> {

    @Test
    @SneakyThrows
    void testAccountsListWithConsentUsingRedirect() {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_accounts_running();
        when()
                .open_banking_list_accounts_called()
                .and()
                .open_banking_user_anton_brueckner_provided_initial_parameters_to_list_accounts();
        then()
                .open_banking_reads_anton_brueckner_accounts_on_redirect();
    }

    @Test
    void testTransactionsListWithConsentUsingRedirect() {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_transactions_running();
        when()
                .open_banking_list_transactions_called_for_anton_brueckner()
                .and()
                .open_banking_user_anton_brueckner_provided_initial_parameters_to_list_transactions();
        then()
                .open_banking_reads_anton_brueckner_transactions_on_redirect();
    }

    @Test
    void testAccountsListWithConsentUsingEmbedded() {
        given()
                .embedded_mock_of_sandbox_for_max_musterman_accounts_running();
        when()
                .open_banking_list_accounts_called()
                .and()
                .open_banking_user_max_musterman_provided_initial_parameters()
                .and()
                .open_banking_user_max_musterman_provided_password()
                .and()
                .open_banking_user_max_musterman_selected_sca_challenge_type()
                .and()
                .open_banking_user_max_musterman_provided_sca_challenge_result_and_no_redirect();
        then()
                .open_banking_has_max_musterman_accounts();
    }

    @Test
    void testTransactionsListWithConsentUsingEmbedded() {
    }
}

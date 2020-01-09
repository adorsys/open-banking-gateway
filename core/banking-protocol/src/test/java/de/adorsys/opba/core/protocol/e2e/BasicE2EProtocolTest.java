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
                .redirect_sandbox_mock_running();
        when()
                .open_banking_list_accounts_called()
                .and()
                .open_banking_user_provided_necessary_details();
        then()
                .obg_reads_result_on_redirect();
    }

    @Test
    void testTransactionsListWithConsentUsingRedirect() {
    }

    @Test
    void testAccountsListWithConsentUsingEmbedded() {
    }

    @Test
    void testTransactionsListWithConsentUsingEmbedded() {
    }
}

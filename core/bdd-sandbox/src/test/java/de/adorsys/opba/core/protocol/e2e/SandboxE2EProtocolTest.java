package de.adorsys.opba.core.protocol.e2e;

import com.jayway.jsonpath.JsonPath;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.core.protocol.BankingProtocol;
import de.adorsys.opba.core.protocol.e2e.stages.AccountInformationRequest;
import de.adorsys.opba.core.protocol.e2e.stages.AccountInformationResult;
import de.adorsys.opba.core.protocol.e2e.stages.real.RealServers;
import de.adorsys.psd2.sandbox.cms.starter.Xs2aCmsAutoConfiguration;
import de.sandbox.openbankinggateway.sandbox.SandboxAppsStarter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static de.adorsys.opba.core.protocol.Const.SKIP_HEAVY_TESTS;
import static de.adorsys.opba.core.protocol.TestProfiles.MOCKED_SANDBOX;
import static de.adorsys.opba.core.protocol.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Happy-path heavy test that uses Dynamic-Sandbox to drive banking-protocol.
 */
@DisabledIfSystemProperty(named = SKIP_HEAVY_TESTS, matches = "true")
@EnableAutoConfiguration(exclude = {
        HypermediaAutoConfiguration.class,
        Xs2aCmsAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class,
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {BankingProtocol.class, JGivenConfig.class}, webEnvironment = RANDOM_PORT)
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS, MOCKED_SANDBOX})
class SandboxE2EProtocolTest extends SpringScenarioTest<RealServers, AccountInformationRequest, AccountInformationResult> {

    private static final SandboxAppsStarter executor = new SandboxAppsStarter();

    @BeforeAll
    static void startSandbox() {
        executor.runAll();
        executor.awaitForAllStarted();
    }

    @AfterAll
    static void stopSandbox() {
        executor.shutdown();
    }

    @Test
    void testAccountsListWithConsentUsingEmbedded() {
        embeddedListMaxMustermanAccounts();
    }

    @Test
    void testTransactionsListWithConsentUsingEmbedded() {
        String accountResourceId = JsonPath.parse(embeddedListMaxMustermanAccounts()).read("$.[0].resourceId");

        given()
                .enabled_embedded_sandbox_mode();
        when()
                .open_banking_list_transactions_called_for_max_musterman(accountResourceId)
                .and()
                .open_banking_user_max_musterman_provided_initial_parameters_to_list_transactions()
                .and()
                .open_banking_user_max_musterman_provided_password()
                .and()
                .open_banking_user_max_musterman_selected_sca_challenge_type_email1()
                .and()
                .open_banking_user_max_musterman_provided_sca_challenge_result_and_no_redirect();
        then()
                .open_banking_has_max_musterman_transactions_validated_by_iban();
    }

    private String embeddedListMaxMustermanAccounts() {
        given()
            .enabled_embedded_sandbox_mode();
        when()
            .open_banking_list_accounts_called()
            .and()
            .open_banking_user_max_musterman_provided_initial_parameters_to_list_accounts()
            .and()
            .open_banking_user_max_musterman_provided_password()
            .and()
            .open_banking_user_max_musterman_selected_sca_challenge_type_email2()
            .and()
            .open_banking_user_max_musterman_provided_sca_challenge_result_and_no_redirect();

        AccountInformationResult result = then()
                .open_banking_has_max_musterman_accounts();

        return result.getResponseContent();
    }
}

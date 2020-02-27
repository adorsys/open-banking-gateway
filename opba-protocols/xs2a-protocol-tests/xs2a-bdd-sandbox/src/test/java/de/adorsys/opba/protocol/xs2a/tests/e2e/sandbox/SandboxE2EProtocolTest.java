package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox;

import com.jayway.jsonpath.JsonPath;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.db.config.EnableBankingPersistence;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.SandboxServers;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.WebDriverBasedAccountInformation;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import de.adorsys.opba.protocol.xs2a.testsandbox.SandboxAppsStarter;
import de.adorsys.psd2.sandbox.cms.starter.Xs2aCmsAutoConfiguration;
import io.github.bonigarcia.seljup.SeleniumExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.MOCKED_SANDBOX;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static de.adorsys.opba.protocol.xs2a.testsandbox.Const.ENABLE_HEAVY_TESTS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Happy-path heavy test that uses Dynamic-Sandbox to drive banking-protocol.
 */
@EnabledIfEnvironmentVariable(named = ENABLE_HEAVY_TESTS, matches = "true")
@EnableAutoConfiguration(exclude = {
        HypermediaAutoConfiguration.class,
        Xs2aCmsAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class,
})
@EnableBankingPersistence
@ExtendWith(SeleniumExtension.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {Xs2aSandboxProtocolApplication.class, JGivenConfig.class}, webEnvironment = RANDOM_PORT)
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS, MOCKED_SANDBOX})
class SandboxE2EProtocolTest extends SpringScenarioTest<SandboxServers, WebDriverBasedAccountInformation<? extends WebDriverBasedAccountInformation<?>>, AccountInformationResult> {

    private static final SandboxAppsStarter executor = new SandboxAppsStarter();

    @LocalServerPort
    private int port;

    @Autowired
    private ProtocolConfiguration configuration;

    @BeforeAll
    static void startSandbox() {
        if (null != System.getenv("NO_SANDBOX_START")) {
            return;
        }

        executor.runAll();
        executor.awaitForAllStarted();
    }

    @AfterAll
    static void stopSandbox() {
        executor.shutdown();
    }

    // See https://github.com/spring-projects/spring-boot/issues/14879 for the 'why setting port'
    @BeforeEach
    void setBaseUrl() {
        ProtocolConfiguration.Redirect.Consent consent = configuration.getRedirect().getConsentAccounts();
        consent.setOk(consent.getOk().replaceAll("localhost:\\d+", "localhost:" + port));
        consent.setNok(consent.getNok().replaceAll("localhost:\\d+", "localhost:" + port));
    }

    @Test
    public void testAccountsListWithConsentUsingRedirect(FirefoxDriver firefoxDriver) {
        redirectListAntonBruecknerAccounts(firefoxDriver);
    }

    @Test
    public void testTransactionListWithConsentUsingRedirect(FirefoxDriver firefoxDriver) {
        String accountResourceId = JsonPath
                .parse(redirectListAntonBruecknerAccounts(firefoxDriver)).read("$.[0].resourceId");

        given()
                .enabled_redirect_sandbox_mode();

        when()
                .open_banking_list_transactions_called_for_anton_brueckner(accountResourceId)
                .and()
                .open_banking_user_anton_brueckner_provided_initial_parameters_to_list_transactions()
                .and()
                .sandbox_anton_brueckner_navigates_to_bank_auth_page(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_inputs_username_and_password(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_confirms_consent_information(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_selects_sca_method(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_provides_sca_challenge_result(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_see_redirect_back_to_tpp_button(firefoxDriver);

        then()
                .open_banking_reads_anton_brueckner_transactions_validated_by_iban();
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
            .open_banking_list_accounts_called_for_max_musterman()
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

    private String redirectListAntonBruecknerAccounts(FirefoxDriver firefoxDriver) {
        given()
                .enabled_redirect_sandbox_mode();

        when()
                .open_banking_list_accounts_called_for_anton_brueckner()
                .and()
                .open_banking_user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent()
                .and()
                .sandbox_anton_brueckner_navigates_to_bank_auth_page(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_inputs_username_and_password(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_confirms_consent_information(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_selects_sca_method(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_provides_sca_challenge_result(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_see_redirect_back_to_tpp_button(firefoxDriver);

        AccountInformationResult result = then()
                .open_banking_reads_anton_brueckner_accounts_on_redirect();

        return result.getResponseContent();
    }
}

package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox;

import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.SandboxServers;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.WebDriverBasedPaymentInitiation;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.config.RetryableConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentResult;
import de.adorsys.psd2.sandbox.cms.starter.Xs2aCmsAutoConfiguration;
import io.github.bonigarcia.seljup.SeleniumExtension;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.Const.ENABLE_HEAVY_TESTS;
import static de.adorsys.opba.protocol.xs2a.tests.Const.TRUE_BOOL;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.MOCKED_SANDBOX;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Happy-path heavy test that uses Dynamic-Sandbox to drive banking-protocol.
 */
@EnabledIfEnvironmentVariable(named = ENABLE_HEAVY_TESTS, matches = TRUE_BOOL)
@EnableAutoConfiguration(exclude = {
    HypermediaAutoConfiguration.class,
    Xs2aCmsAutoConfiguration.class,
    ManagementWebSecurityAutoConfiguration.class,
    SecurityAutoConfiguration.class,
})
@ExtendWith(SeleniumExtension.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {RetryableConfig.class, Xs2aRealSandboxProtocolApplication.class, JGivenConfig.class}, webEnvironment = RANDOM_PORT)
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS, MOCKED_SANDBOX})
public class SandboxE2EProtocolPisTest extends SandboxCommonTest<
        SandboxServers<? extends SandboxServers<?>>,
        WebDriverBasedPaymentInitiation<? extends WebDriverBasedPaymentInitiation<?>>,
        PaymentResult<? extends PaymentResult<?>>> {

    private final String OPBA_LOGIN = UUID.randomUUID().toString();
    private final String OPBA_PASSWORD = UUID.randomUUID().toString();

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testSinglePaymentUsingEmbedded(Approach expectedApproach) {
        given()
                .enabled_embedded_sandbox_mode()
                .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
                .rest_assured_points_to_opba_server()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);
        when()
                .fintech_calls_initiate_payment_for_max_musterman()
                .and()
                .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url_pis(OPBA_LOGIN, OPBA_PASSWORD)
                .and()
                .user_max_musterman_provided_initial_parameters_to_make_payment()
                .and()
                .user_max_musterman_provided_password_to_embedded_authorization()
                .and()
                .user_max_musterman_selected_sca_challenge_type_email2_to_embedded_authorization()
                .and()
                .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok_pis();

        then()
                .open_banking_has_consent_for_max_musterman_payment()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .fintech_calls_payment_status()
                .fintech_calls_payment_information();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testSinglePaymentUsingRedirect(Approach expectedApproach, FirefoxDriver firefoxDriver) {
        given()
                .enabled_redirect_sandbox_mode()
                .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
                .rest_assured_points_to_opba_server()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_initiate_payment_for_anton_brueckner()
                .and()
                .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url_pis(OPBA_LOGIN, OPBA_PASSWORD)
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent()
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp_pis()
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
                .sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only(firefoxDriver);
        then()
                .open_banking_has_consent_for_anton_brueckner_payment()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .fintech_calls_payment_status()
                .fintech_calls_payment_information();
    }
}

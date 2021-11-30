package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox;

import com.github.tomakehurst.wiremock.WireMockServer;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.SandboxServers;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.WebDriverBasedPaymentInitiation;
import de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.config.RetryableConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentResult;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.RedirectCapturingTransformer;
import de.adorsys.psd2.sandbox.cms.starter.Xs2aCmsAutoConfiguration;
import io.github.bonigarcia.seljup.SeleniumExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static de.adorsys.opba.protocol.xs2a.tests.Const.ENABLE_HEAVY_TESTS;
import static de.adorsys.opba.protocol.xs2a.tests.Const.TRUE_BOOL;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.MOCKED_SANDBOX;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Separate class for OAuth2 pre-step as Sandbox gets stuck in this mode.
 */
@SuppressWarnings("CPD-START") // Same steps are used, but that's fine for readability
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
class SandboxE2EProtocolPisOauth2Test extends SandboxCommonTest<
        SandboxServers<? extends SandboxServers<?>>,
        WebDriverBasedPaymentInitiation<? extends WebDriverBasedPaymentInitiation<?>>,
        PaymentResult<? extends PaymentResult<?>>> {

    // Special hack to handle 202 when returning from ASPSP and replacing it with 303
    private WireMockServer wireMockRedirectServer;
    private final RedirectCapturingTransformer transformer = new RedirectCapturingTransformer();

    // See https://github.com/spring-projects/spring-boot/issues/14879 for the 'why setting port'
    @BeforeEach
    void setBaseUrl() {
        transformer.setObgPort(port);
        wireMockRedirectServer = new WireMockServer(wireMockConfig().extensions(transformer).dynamicPort());
        wireMockRedirectServer.start();
        wireMockRedirectServer.stubFor(get(urlMatching(".*/fromAspsp/.*")).willReturn(aResponse().withStatus(200)));

        ProtocolUrlsConfiguration.WebHooks aisUrls = urlsConfiguration.getAis().getWebHooks();
        aisUrls.setOk(aisUrls.getOk().replaceAll("localhost:\\d+", "localhost:" + wireMockRedirectServer.port()));
        aisUrls.setNok(aisUrls.getNok().replaceAll("localhost:\\d+", "localhost:" + wireMockRedirectServer.port()));

        ProtocolUrlsConfiguration.WebHooks pisUrls = urlsConfiguration.getPis().getWebHooks();
        pisUrls.setOk(pisUrls.getOk().replaceAll("localhost:\\d+", "localhost:" + wireMockRedirectServer.port()));
        pisUrls.setNok(pisUrls.getNok().replaceAll("localhost:\\d+", "localhost:" + wireMockRedirectServer.port()));
    }

    @AfterEach
    void stop303redirectWiremock() {
        wireMockRedirectServer.stop();
    }

    /**
     * Not using {@code ParameterizedTest} as OAuth2 is the special case of REDIRECT (to reduce pipeline runtime).
     */
    @Test
    public void testSinglePaymentWithConsentUsingOAuth2PreStep(FirefoxDriver firefoxDriver) {
        given()
                .enabled_oauth2_pre_step_sandbox_mode()
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_initiate_payment_for_anton_brueckner()
                .and()
                .user_logged_in_into_opba_pis_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
                .and()
                .set_auth_session_key_in_wiremock_transformer(transformer)
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_authorize_initiation_payment()
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
                .and()
                .sandbox_anton_brueckner_navigates_to_bank_auth_page(firefoxDriver)
                .and()
                .add_open_banking_auth_session_key_cookie_to_selenium(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_inputs_username_and_password_for_oauth2_form(firefoxDriver)
                .and()
                .update_redirect_code_from_browser_url_oauth2(firefoxDriver)
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
                .and()
                .sandbox_anton_brueckner_navigates_to_bank_auth_page(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_inputs_username_and_password_for_oauth2_form(firefoxDriver) // FIXME The step introduced with Sandbox 5.14, it is not clear why
                .and()
                .sandbox_anton_brueckner_confirms_consent_information(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_selects_sca_method(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_provides_sca_challenge_result(firefoxDriver)
                .and()
                .sandbox_anton_brueckner_clicks_redirect_back_to_tpp_button_api_localhost_cookie_only(firefoxDriver);

        then()
                .open_banking_has_stored_payment()
                .fintech_calls_payment_activation_for_current_authorization_id()
                .fintech_calls_payment_status()
                .fintech_calls_payment_information_iban_400();
    }
}

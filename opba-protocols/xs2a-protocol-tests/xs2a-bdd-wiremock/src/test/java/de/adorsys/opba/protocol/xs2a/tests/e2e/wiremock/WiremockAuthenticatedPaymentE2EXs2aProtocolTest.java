package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentResult;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.MockServers;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockPaymentRequest;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.Xs2aProtocolApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.MOCKED_SANDBOX;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Happy-path test that uses wiremock-stubbed request-responses to drive banking-protocol.
 */
@SuppressWarnings("CPD-START") // Makes no sense to be too abstract
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {Xs2aProtocolApplication.class, JGivenConfig.class}, webEnvironment = RANDOM_PORT)
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS, MOCKED_SANDBOX})
public class WiremockAuthenticatedPaymentE2EXs2aProtocolTest extends SpringScenarioTest<MockServers, WiremockPaymentRequest<? extends WiremockPaymentRequest<?>>, PaymentResult> {

    private final String OPBA_PASSWORD = UUID.randomUUID().toString();
    private final String OPBA_LOGIN = UUID.randomUUID().toString();

    @Autowired
    private ProtocolUrlsConfiguration urlsConfiguration;

    @LocalServerPort
    private int port;

    // See https://github.com/spring-projects/spring-boot/issues/14879 for the 'why setting port'
    @BeforeEach
    void setBaseUrl() {
        ProtocolUrlsConfiguration.WebHooks pisUrls = urlsConfiguration.getPis().getWebHooks();
        pisUrls.setOk(pisUrls.getOk().replaceAll("localhost:\\d+", "localhost:" + port));
        pisUrls.setNok(pisUrls.getNok().replaceAll("localhost:\\d+", "localhost:" + port));
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testAnonymousPaymentInitializationImpossibleUsingRedirect(Approach expectedApproach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_payments_running()
                .set_default_preferred_approach()
                .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
                .rest_assured_points_to_opba_server()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_initiate_payment_for_anton_brueckner();

        then()
            .user_logged_in_into_opba_as_anonymous_user_with_credentials_using_fintech_supplied_url_is_forbidden();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testPaymentInitializationUsingRedirect(Approach expectedApproach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_payments_running()
                .set_default_preferred_approach()
                .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
                .rest_assured_points_to_opba_server()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_initiate_payment_for_anton_brueckner()
                .and()
                .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_authorize_initiation_payment()
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test();
        then()
                .open_banking_has_stored_payment()
                .fintech_calls_payment_activation_for_current_authorization_id()
                .fintech_calls_payment_status();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testPaymentInitializationUsingRedirectWithTppRedirectPreferredTrue(Approach expectedApproach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_payments_running()
                .set_tpp_redirect_preferred_true()
                .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
                .rest_assured_points_to_opba_server()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_initiate_payment_for_anton_brueckner()
                .and()
                .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_authorize_initiation_payment()
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test();
        then()
                .open_banking_has_stored_payment()
                .fintech_calls_payment_activation_for_current_authorization_id()
                .fintech_calls_payment_status();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testPaymentInitializationUsingRedirectWithTppRedirectPreferredFalse(Approach expectedApproach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_payments_running()
                .set_tpp_redirect_preferred_false()
                .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
                .rest_assured_points_to_opba_server()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_initiate_payment_for_anton_brueckner()
                .and()
                .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_authorize_initiation_payment()
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test();
        then()
                .open_banking_has_stored_payment()
                .fintech_calls_payment_activation_for_current_authorization_id()
                .fintech_calls_payment_status();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testPaymentInitializationUsingRedirectWithCookieValidation(Approach expectedApproach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_payments_running()
                .set_default_preferred_approach()
                .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
                .rest_assured_points_to_opba_server()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_initiate_payment_for_anton_brueckner()
                .and()
                .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_authorize_initiation_payment_without_cookie_unauthorized()
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_authorize_initiation_payment()
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp_without_cookie_unauthorized()
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test_without_cookie_unauthorized()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test();
        then()
                .open_banking_has_stored_payment()
                .fintech_calls_payment_activation_for_current_authorization_id()
                .fintech_calls_payment_status();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testPaymentInitializationUsingRedirectWithCookieValidationWithTppRedirectPreferredTrue(Approach expectedApproach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_payments_running()
                .set_tpp_redirect_preferred_true()
                .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
                .rest_assured_points_to_opba_server()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_initiate_payment_for_anton_brueckner()
                .and()
                .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_authorize_initiation_payment_without_cookie_unauthorized()
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_authorize_initiation_payment()
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp_without_cookie_unauthorized()
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test_without_cookie_unauthorized()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test();
        then()
                .open_banking_has_stored_payment()
                .fintech_calls_payment_activation_for_current_authorization_id()
                .fintech_calls_payment_status();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testPaymentInitializationUsingRedirectWithCookieValidationWithTppRedirectPreferredFalse(Approach expectedApproach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_payments_running()
                .set_tpp_redirect_preferred_false()
                .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
                .rest_assured_points_to_opba_server()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_initiate_payment_for_anton_brueckner()
                .and()
                .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_authorize_initiation_payment_without_cookie_unauthorized()
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_authorize_initiation_payment()
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp_without_cookie_unauthorized()
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test_without_cookie_unauthorized()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test();
        then()
                .open_banking_has_stored_payment()
                .fintech_calls_payment_activation_for_current_authorization_id()
                .fintech_calls_payment_status();
    }
}


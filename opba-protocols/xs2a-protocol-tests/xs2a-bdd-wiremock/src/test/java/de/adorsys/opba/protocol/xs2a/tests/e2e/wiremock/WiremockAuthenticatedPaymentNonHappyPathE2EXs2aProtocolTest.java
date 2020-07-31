package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.NonHappyPaymentResult;
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
 * Non happy-path test that uses wiremock-stubbed request-responses to drive banking-protocol.
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {Xs2aProtocolApplication.class, JGivenConfig.class}, webEnvironment = RANDOM_PORT)
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS, MOCKED_SANDBOX})
public class WiremockAuthenticatedPaymentNonHappyPathE2EXs2aProtocolTest extends SpringScenarioTest<MockServers, WiremockPaymentRequest<? extends WiremockPaymentRequest<?>>, NonHappyPaymentResult> {

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
    void testPaymentInitializationUsingRedirectPaymentDenialPossible(Approach expectedApproach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_payments_running()
                .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
                .rest_assured_points_to_opba_server()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_initiate_payment_for_anton_brueckner_with_anonymous_allowed()
                .and()
                .user_logged_in_into_opba_as_anonymous_user_with_credentials_using_fintech_supplied_url();

        then()
                .user_anton_brueckner_requests_payment_denial_and_he_is_redirected_back_to_fintech_ok();
    }

    @ParameterizedTest
    @EnumSource(Approach.class)
    void testPaymentInitializationUsingRedirectPaymentDenialImpossible(Approach expectedApproach) {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_payments_running()
                .preferred_sca_approach_selected_for_all_banks_in_opba(expectedApproach)
                .rest_assured_points_to_opba_server()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_initiate_payment_for_anton_brueckner_with_anonymous_allowed()
                .and()
                .user_logged_in_into_opba_as_anonymous_user_with_credentials_using_fintech_supplied_url()
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_authorize_initiation_payment()
                .and()
                .user_anton_brueckner_sees_that_he_needs_to_be_redirected_to_aspsp_and_redirects_to_aspsp()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test();

        then()
                .user_anton_brueckner_requests_payment_denial_and_it_is_impossible_as_payment_is_authorized();
    }
}

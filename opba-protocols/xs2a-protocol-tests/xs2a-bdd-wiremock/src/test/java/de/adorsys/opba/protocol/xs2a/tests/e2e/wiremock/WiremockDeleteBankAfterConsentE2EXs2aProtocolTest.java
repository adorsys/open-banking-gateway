package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.MockServers;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockAccountInformationRequest;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.Xs2aProtocolApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.MOCKED_SANDBOX;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.MockServers.SANDBOX_BANK_ID;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Sql(statements = "UPDATE opb_bank_action SET protocol_bean_name = 'xs2aListTransactions' WHERE protocol_bean_name = 'xs2aSandboxListTransactions'")

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {Xs2aProtocolApplication.class, JGivenConfig.class}, webEnvironment = RANDOM_PORT, properties = {"spring.datasource.url=jdbc:tc:postgresql:12:////open_banking?TC_INITSCRIPT=init.sql?TC_TMPFS=/testtmpfs2:rw"})
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS, MOCKED_SANDBOX})
class WiremockDeleteBankAfterConsentE2EXs2aProtocolTest extends SpringScenarioTest<MockServers, WiremockAccountInformationRequest<? extends WiremockAccountInformationRequest<?>>, AccountInformationResult> {

    private final String OPBA_LOGIN = UUID.randomUUID().toString();
    private final String OPBA_PASSWORD = UUID.randomUUID().toString();

    @LocalServerPort
    private int port;

    @Autowired
    private ProtocolUrlsConfiguration urlsConfiguration;

    @BeforeEach
    void setBaseUrl() {
        ProtocolUrlsConfiguration.WebHooks aisUrls = urlsConfiguration.getAis().getWebHooks();
        aisUrls.setOk(aisUrls.getOk().replaceAll("localhost:\\d+", "localhost:" + port));
        aisUrls.setNok(aisUrls.getNok().replaceAll("localhost:\\d+", "localhost:" + port));
    }


    @Test
    void testBankDeletionAfterAccountsListWithConsentUsingRedirect() {
        given()
                .redirect_mock_of_sandbox_for_anton_brueckner_accounts_running()
                .set_default_preferred_approach()
                .preferred_sca_approach_selected_for_all_banks_in_opba(Approach.REDIRECT)
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_list_accounts_for_anton_brueckner()
                .and()
                .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent_with_ip_address_check()
                .and()
                .open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test()
                .and()
                .admin_calls_delete_bank(SANDBOX_BANK_ID);

        then()
                .admin_check_that_bank_is_deleted(SANDBOX_BANK_ID)
                .open_banking_has_no_consent_for_anton_brueckner_account_list()
                .fintech_calls_consent_activation_for_current_authorization_id_failed_with_not_found()
                .open_banking_can_not_read_anton_brueckner_account_data_using_consent_bound_to_service_session_and_bank_profile_id();
    }
}

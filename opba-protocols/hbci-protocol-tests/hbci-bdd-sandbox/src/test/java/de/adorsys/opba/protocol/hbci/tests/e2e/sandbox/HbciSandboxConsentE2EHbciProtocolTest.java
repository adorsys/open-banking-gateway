package de.adorsys.opba.protocol.hbci.tests.e2e.sandbox;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.hbci.config.HbciAdapterProperties;
import de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.HbciAccountInformationRequest;
import de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.HbciAccountInformationResult;
import de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.HbciServers;
import de.adorsys.opba.protocol.sandbox.hbci.HbciServerApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.Const.HBCI_SANDBOX_CONFIG;
import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.BANK_BLZ_20000002_ID;
import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.BANK_BLZ_30000003_ID;
import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.MAX_MUSTERMAN_BANK_BLZ_20000002_ACCOUNT_ID;
import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.MOCKED_SANDBOX;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockConst.BOTH_BOOKING;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockConst.DATE_FROM;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockConst.DATE_TO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Happy-path test that uses HBCI Sandbox to drive banking-protocol.
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {
        HbciProtocolApplication.class,
        HbciServerApplication.class,  // Starting HBCI server within test so that application basically communicates with itself
        HbciJGivenConfig.class
}, webEnvironment = RANDOM_PORT)
@Disabled
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS, MOCKED_SANDBOX, HBCI_SANDBOX_CONFIG})
class HbciSandboxConsentE2EHbciProtocolTest extends SpringScenarioTest<
        HbciServers,
        HbciAccountInformationRequest<? extends HbciAccountInformationRequest<?>>,
        HbciAccountInformationResult<? extends HbciAccountInformationResult<?>>
    > {

    private final String OPBA_LOGIN = UUID.randomUUID().toString();
    private final String OPBA_PASSWORD = UUID.randomUUID().toString();

    @LocalServerPort
    private int port;

    @Autowired
    private HbciAdapterProperties adapterProperties;

    @Autowired
    private BankProfileJpaRepository bankProfileJpaRepository;

    @SpyBean
    OnlineBankingService onlineBankingService;

    // TODO: Those dependencies do not need to be mocked, but should be optional
    // Stubbing out xs2a protocol declared dependencies:
    @MockBean
    @SuppressWarnings("PMD.UnusedPrivateField") // Used to make Spring happy
    private DtoMapper<Set<ValidationIssue>, Set<ValidationError>> dtoMapper;

    // See https://github.com/spring-projects/spring-boot/issues/14879 for the 'why setting port'
    @BeforeEach
    @Transactional
    void setBaseUrl() {
        makeHbciAdapterToPointToHbciMockEndpoints();
    }

    @ValueSource(booleans = {false, true})
    @ParameterizedTest
    void testAccountsListWithConsentNoScaButUserHasOneSca(boolean online) {
        given()
            .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
            .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);
        when()
            .fintech_calls_list_accounts_for_anton_brueckner_for_blz_30000003(online)
            .and()
            .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
            .and()
            .user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent()
            .and()
            .user_anton_brueckner_provided_correct_pin_to_embedded_authorization_and_sees_redirect_to_fintech_ok();
        then()
            .open_banking_has_consent_for_anton_brueckner_account_list()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_can_read_anton_brueckner_hbci_account_data_using_consent_bound_to_service_session_bank_blz_30000003();
    }

    @Test
    @Disabled // FIXME - fix issue with concurrent BPD retrieval - only one test works
    void testAccountsListWithConsentNoScaForAll() {
        given()
            .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
            .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);
        when()
            .fintech_calls_list_accounts_max_musterman_for_blz_20000002()
            .and()
            .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
            .and()
            .user_max_musterman_provided_initial_parameters_to_list_transactions_with_all_accounts_psd2_consent()
            .and()
            .user_max_musterman_provided_correct_pin_to_embedded_authorization_and_sees_redirect_to_fintech_ok();
        then()
            .open_banking_has_consent_for_max_musterman_account_list()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_can_read_max_musterman_hbci_account_data_using_consent_bound_to_service_session_bank_blz_20000002();
    }

    @Test
    void testAccountsListWithConsentNoScaButUserHasMultiSca() {
        given()
            .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
            .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);
        when()
            .fintech_calls_list_accounts_for_max_musterman_for_blz_30000003()
            .and()
            .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
            .and()
            .user_max_musterman_provided_initial_parameters_to_list_accounts_all_accounts_consent()
            .and()
            .user_max_musterman_provided_password_to_embedded_authorization()
            .and()
            .user_max_musterman_selected_sca_challenge_type_push_tan_to_embedded_authorization(); // FIXME: Is a glitch for this user, tan will not be used
        then()
            .open_banking_has_consent_for_max_musterman_account_list()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_can_read_max_musterman_hbci_account_data_using_consent_bound_to_service_session_bank_blz_30000003();
    }

    @Test
    void testAccountsListNoScaThenTransactionListNoSca() {
        testAccountsListWithConsentNoScaForAll();

        when()
            .fintech_calls_list_transactions_for_max_musterman_using_already_granted_service_session(MAX_MUSTERMAN_BANK_BLZ_20000002_ACCOUNT_ID, BANK_BLZ_20000002_ID)
            .and()
            .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
            .and()
            .user_max_musterman_provided_initial_parameters_to_list_transactions_with_all_accounts_psd2_consent()
            .and()
            .user_max_musterman_provided_correct_pin_to_embedded_authorization_and_sees_redirect_to_fintech_ok();
        then()
            .open_banking_has_consent_for_max_musterman_transaction_list()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_can_read_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session_bank_blz_20000002(
                    MAX_MUSTERMAN_BANK_BLZ_20000002_ACCOUNT_ID, DATE_FROM, DATE_TO, BOTH_BOOKING
            );
    }

    @Test
    void testAccountsListNoScaThenTransactionListWithSca() {
        testAccountsListWithConsentNoScaButUserHasMultiSca();

        when()
            .fintech_calls_list_transactions_for_max_musterman_using_already_granted_service_session(MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID, BANK_BLZ_30000003_ID)
            .and()
            .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
            .and()
            .user_max_musterman_provided_initial_parameters_to_list_transactions_with_all_accounts_psd2_consent()
            .and()
            .user_max_musterman_provided_password_to_embedded_authorization()
            .and()
            .user_max_musterman_selected_sca_challenge_type_push_tan_to_embedded_authorization()
            .and()
            .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok("/PUSH_OTP");
        then()
            .open_banking_has_consent_for_max_musterman_transaction_list()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_can_read_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session_bank_blz_30000003(
                    MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID, DATE_FROM, DATE_TO, BOTH_BOOKING
            );
    }

    @Test
    void testDirectTransactionListWithSca() {
        given()
            .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
            .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);
        when()
            .fintech_calls_list_transactions_for_max_musterman_for_blz_30000003()
            .and()
            .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
            .and()
            .user_max_musterman_provided_initial_parameters_to_list_transactions_with_single_account_consent()
            .and()
            .user_max_musterman_provided_password_to_embedded_authorization()
            .and()
            .user_max_musterman_selected_sca_challenge_type_push_tan_to_embedded_authorization()
            .and()
            .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok("/PUSH_OTP");
        then()
            .open_banking_has_consent_for_max_musterman_account_list()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_can_read_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session_bank_blz_30000003(
                    MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID, DATE_FROM, DATE_TO, BOTH_BOOKING
            );
    }

    @Test
    void testDirectTransactionListWithScaAndBookingDate() {
        given()
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);
        when()
                .fintech_calls_list_transactions_for_max_musterman_for_blz_30000003()
                .and()
                .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
                .and()
                .user_max_musterman_provided_initial_parameters_to_list_transactions_with_single_account_consent()
                .and()
                .user_max_musterman_provided_password_to_embedded_authorization()
                .and()
                .user_max_musterman_selected_sca_challenge_type_push_tan_to_embedded_authorization()
                .and()
                .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok("/PUSH_OTP");
        then()
                .open_banking_has_consent_for_max_musterman_account_list()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .open_banking_can_read_empty_due_to_range_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session(
                        MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID, BANK_BLZ_30000003_ID, DATE_FROM, DATE_FROM, BOTH_BOOKING
                );
    }

    @Test
    @Disabled // FIXME - fix issue with concurrent BPD retrieval - only one test works
    void testDirectTransactionListWithoutSca() {
        given()
            .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
            .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);
        when()
            .fintech_calls_list_transactions_for_max_musterman_for_blz_20000002()
            .and()
            .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
            .and()
            .user_max_musterman_provided_initial_parameters_to_list_transactions_with_single_account_consent()
            .and()
            .user_max_musterman_provided_correct_pin_to_embedded_authorization_and_sees_redirect_to_fintech_ok();
        then()
            .open_banking_has_consent_for_max_musterman_account_list()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_can_read_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session_bank_blz_20000002(
                    MAX_MUSTERMAN_BANK_BLZ_20000002_ACCOUNT_ID, DATE_FROM, DATE_TO, BOTH_BOOKING
            );
    }

    @Test
    void testAccountsListCacheUpdate() {
        testAccountsListWithConsentNoScaButUserHasOneSca(false);
        verify(onlineBankingService, times(1)).loadBankAccounts(any());

        then().open_banking_can_read_anton_brueckner_hbci_account_data_using_consent_bound_to_service_session_bank_blz_30000003();
        verify(onlineBankingService, times(1)).loadBankAccounts(any());

        testAccountsListWithConsentNoScaButUserHasOneSca(true);
        verify(onlineBankingService, times(2)).loadBankAccounts(any());
    }

    @Test
    void testTransactionListCacheUpdate() {
        testDirectTransactionListWithSca();
    }

    @Test
    void testAccountListWrongPinThenOk() {
        given()
                .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);
        when()
                .fintech_calls_list_accounts_for_anton_brueckner_for_blz_30000003(true)
                .and()
                .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
                .and()
                .user_anton_brueckner_provided_initial_parameters_to_list_accounts_with_all_accounts_consent()
                .and()
                .user_anton_brueckner_provided_incorrect_pin_to_embedded_authorization_and_returns_to_ask_pin()
                .and()
                .user_anton_brueckner_provided_correct_pin_to_embedded_authorization_and_sees_redirect_to_fintech_ok();
        then()
                .open_banking_has_consent_for_anton_brueckner_account_list()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .open_banking_can_read_anton_brueckner_hbci_account_data_using_consent_bound_to_service_session_bank_blz_30000003();
    }

    @Test
    void testTransactionListWithWrongPinThenOkWrongTanThenOk() {
        testAccountsListWithConsentNoScaButUserHasMultiSca();

        when()
                .fintech_calls_list_transactions_for_max_musterman_using_already_granted_service_session(MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID, BANK_BLZ_30000003_ID)
                .and()
                .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
                .and()
                .user_max_musterman_provided_initial_parameters_to_list_transactions_with_all_accounts_psd2_consent()
                .and()
                .user_max_musterman_provided_wrong_password_to_embedded_authorization_and_stays_on_password_page()
                .and()
                .user_max_musterman_provided_correct_password_after_wrong_to_embedded_authorization()
                .and()
                .user_max_musterman_selected_sca_challenge_type_push_tan_to_embedded_authorization()
                .and()
                .user_max_musterman_provided_wrong_sca_challenge_result_to_embedded_authorization_and_redirected_to_select_sca()
                .and()
                .user_max_musterman_selected_sca_challenge_type_push_tan_to_embedded_authorization()
                .and()
                .user_max_musterman_provided_correct_sca_challenge_result_after_wrong_to_embedded_authorization_and_sees_redirect_to_fintech_ok("/PUSH_OTP");
        then()
                .open_banking_has_consent_for_max_musterman_transaction_list()
                .fintech_calls_consent_activation_for_current_authorization_id()
                .open_banking_can_read_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session_bank_blz_30000003(
                        MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID, DATE_FROM, DATE_TO, BOTH_BOOKING
                );
    }

    private void makeHbciAdapterToPointToHbciMockEndpoints() {
        adapterProperties.getAdorsysMockBanksBlz().stream()
                .flatMap(it -> bankProfileJpaRepository.findByBankBankCode(String.valueOf(it)).stream())
                .map(it -> {
                    it.setUrl("http://localhost:" + port + "/hbci-mock/");
                    return it;
                })
                .forEach(it -> bankProfileJpaRepository.save(it));
    }
}

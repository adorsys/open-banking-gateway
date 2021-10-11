package de.adorsys.opba.protocol.hbci.tests.e2e.sandbox;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.hbci.config.HbciAdapterProperties;
import de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.HbciAccountInformationRequest;
import de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.HbciAccountInformationResult;
import de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.HbciPaymentInitiationRequest;
import de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.HbciPaymentInitiationResult;
import de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.HbciServers;
import de.adorsys.opba.protocol.sandbox.hbci.HbciServerApplication;
import de.adorsys.xs2a.adapter.api.model.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.Const.HBCI_SANDBOX_CONFIG;
import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.BANK_BLZ_30000003_ID;
import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.FixtureConst.MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID;
import static de.adorsys.opba.protocol.sandbox.hbci.service.HbciSandboxPaymentService.MAGIC_FLAG_TO_ACCEPT_PAYMENT_IMMEDIATELY;
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
 * Happy-path payment test that uses HBCI Sandbox to drive banking-protocol.
 */
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = "DELETE FROM opb_hbci_sandbox_payment") // Cleanup after test
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {
        HbciProtocolApplication.class,
        HbciServerApplication.class,  // Starting HBCI server within test so that application basically communicates with itself
        HbciJGivenConfig.class
}, webEnvironment = RANDOM_PORT)
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS, MOCKED_SANDBOX, HBCI_SANDBOX_CONFIG})
class HbciSandboxPaymentAndTransactionsAfterE2EHbciProtocolTest extends SpringScenarioTest<
        HbciServers,
        HbciPaymentInitiationRequest<? extends HbciPaymentInitiationRequest<?>>,
        HbciPaymentInitiationResult<? extends HbciPaymentInitiationResult<?>>
    > {

    private final String OPBA_LOGIN = UUID.randomUUID().toString();
    private final String OPBA_PASSWORD = UUID.randomUUID().toString();

    @LocalServerPort
    private int port;

    @Autowired
    private HbciAdapterProperties adapterProperties;

    @Autowired
    private BankProfileJpaRepository bankProfileJpaRepository;

    @ScenarioStage
    private HbciAccountInformationRequest<? extends HbciAccountInformationRequest<?>> accountInformationRequest;

    @ScenarioStage
    private HbciAccountInformationResult<? extends HbciAccountInformationResult<?>> accountInformationResult;

    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;

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
    void setBaseUrlAndAutowireBeans() {
        autowireCapableBeanFactory.autowireBean(accountInformationRequest);
        autowireCapableBeanFactory.autowireBean(accountInformationResult);
        makeHbciAdapterToPointToHbciMockEndpoints();
    }

    @Test
    void testPaymentWithStatusWithScaAndAfterThatPaymentAppearsInTransactions() {
        makeSinglePaymentWithStatusAndSca();

        getTransactionsAndCheckThatPaymentAppearsInTransactions();
    }

    @Test
    void testUpdateCacheAfterAddingPayments() {
        getTransactions();
        verify(onlineBankingService, times(2)).loadTransactions(any());

        makeSinglePaymentWithStatusAndSca();

        makeInstantPaymentWithStatusAndSca();

        checkPaymentsAreNotInTransactions();
        verify(onlineBankingService, times(2)).loadTransactions(any());

        getTransactionsWithCacheUpdate();
        verify(onlineBankingService, times(4)).loadTransactions(any());
    }

    @Test
    void makeSinglePaymentWithStatusAndSca() {
        given()
            .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
            .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);
        when()
            .fintech_calls_single_payment_for_max_musterman(MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID, BANK_BLZ_30000003_ID, MAGIC_FLAG_TO_ACCEPT_PAYMENT_IMMEDIATELY)
            .and()
            .user_logged_in_into_opba_pis_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
            .and()
            .user_max_musterman_provided_initial_parameters_to_make_payment()
            .and()
            .user_max_musterman_provided_password_to_embedded_authorization()
            .and()
            .user_max_musterman_selected_sca_challenge_type_push_tan_to_embedded_authorization()
            .and()
            .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok("/PUSH_OTP");
        then()
            .open_banking_has_stored_payment()
            .fintech_calls_payment_activation_for_current_authorization_id()
            .fintech_calls_payment_status(BANK_BLZ_30000003_ID, TransactionStatus.ACSC.name());
    }

    @Test
    void makeInstantPaymentWithStatusAndSca() {
        given()
            .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
            .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);
        when()
            .fintech_calls_instant_payment_for_max_musterman(MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID, BANK_BLZ_30000003_ID, MAGIC_FLAG_TO_ACCEPT_PAYMENT_IMMEDIATELY)
            .and()
            .user_logged_in_into_opba_pis_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
            .and()
            .user_max_musterman_provided_initial_parameters_to_make_payment()
            .and()
            .user_max_musterman_provided_password_to_embedded_authorization()
            .and()
            .user_max_musterman_selected_sca_challenge_type_push_tan_to_embedded_authorization()
            .and()
            .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok("/PUSH_OTP");
        then()
            .open_banking_has_stored_payment()
            .fintech_calls_payment_activation_for_current_authorization_id()
            .fintech_calls_payment_status(BANK_BLZ_30000003_ID, TransactionStatus.ACSC.name());
    }

    void getTransactions(BigDecimal[] extraTransactions, boolean online) {
        getTransactions(extraTransactions, online, DATE_TO);
    }

    void getTransactions(BigDecimal[] extraTransactions, boolean online, LocalDate dateTo) {
        given()
            .rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api()
            .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);
        accountInformationRequest
            .fintech_calls_list_transactions_for_max_musterman_for_blz_30000003(online)
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
        accountInformationResult
            .open_banking_has_consent_for_max_musterman_account_list()
            .fintech_calls_consent_activation_for_current_authorization_id()
            .open_banking_can_read_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session_with_extra_transactions(
                    MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID, BANK_BLZ_30000003_ID, DATE_FROM, dateTo, BOTH_BOOKING, extraTransactions
            );
    }

    private void getTransactions() {
        getTransactions(new BigDecimal[]{}, false);
    }

    private void getTransactionsAndCheckThatPaymentAppearsInTransactions() {
        getTransactions(new BigDecimal[]{new BigDecimal("-1.03")}, false, LocalDate.now());
    }

    void getTransactionsWithCacheUpdate() {
        BigDecimal[] extraTransactions = {new BigDecimal("-1.03"), new BigDecimal("-1.03")};
        getTransactions(extraTransactions, true, LocalDate.now());
    }

    private void checkPaymentsAreNotInTransactions() {
        accountInformationResult
                .open_banking_can_read_max_musterman_hbci_transaction_data_using_consent_bound_to_service_session(
                        MAX_MUSTERMAN_BANK_BLZ_30000003_ACCOUNT_ID, BANK_BLZ_30000003_ID, DATE_FROM, DATE_TO, BOTH_BOOKING
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

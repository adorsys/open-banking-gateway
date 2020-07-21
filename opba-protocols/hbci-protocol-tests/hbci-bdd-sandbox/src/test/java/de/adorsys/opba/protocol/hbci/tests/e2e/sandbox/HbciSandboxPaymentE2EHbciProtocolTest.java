package de.adorsys.opba.protocol.hbci.tests.e2e.sandbox;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.hbci.config.HbciAdapterProperties;
import de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.HbciPaymentInitiationRequest;
import de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.HbciPaymentInitiationResult;
import de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.hbcisteps.HbciServers;
import de.adorsys.opba.protocol.sandbox.hbci.HbciServerApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.Const.HBCI_SANDBOX_CONFIG;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.MOCKED_SANDBOX;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Happy-path payment test that uses HBCI Sandbox to drive banking-protocol.
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {
        HbciProtocolApplication.class,
        HbciServerApplication.class,  // Starting HBCI server within test so that application basically communicates with itself
        HbciJGivenConfig.class
}, webEnvironment = RANDOM_PORT)
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS, MOCKED_SANDBOX, HBCI_SANDBOX_CONFIG})
class HbciSandboxPaymentE2EHbciProtocolTest extends SpringScenarioTest<
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

    @Test
    void testSinglePaymentWithSca() {
        given()
                .rest_assured_points_to_opba_server()
                .user_registered_in_opba_with_credentials(OPBA_LOGIN, OPBA_PASSWORD);

        when()
                .fintech_calls_single_payment_for_max_musterman_for_blz_30000003()
                .and()
                .user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(OPBA_LOGIN, OPBA_PASSWORD)
                .and()
                .user_max_musterman_provided_initial_parameters_to_make_payment()
                .and()
                .user_max_musterman_provided_password_to_embedded_authorization()
                .and()
                .user_max_musterman_selected_sca_challenge_type_push_tan_to_embedded_authorization()
                .and()
                .user_max_musterman_provided_sca_challenge_result_to_embedded_authorization_and_sees_redirect_to_fintech_ok_pis();
        then()
                .open_banking_has_consent_for_max_musterman_payment()
                .fintech_calls_payment_activation_for_current_authorization_id();
        // TODO check payment status
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

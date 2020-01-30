package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs Sandbox as json-backed mock.
 */
@Slf4j
@JGivenStage
public class MockServers extends Stage<MockServers> {

    @Autowired
    private BankProfileJpaRepository bankProfileJpaRepository;

    @ProvidedScenarioState
    private WireMockServer sandbox;

    @AfterScenario
    @SneakyThrows
    void stopWireMock() {
        if (null != sandbox) {
            sandbox.stop();
            sandbox = null;
        }
    }

    public void redirect_mock_of_sandbox_for_anton_brueckner_accounts_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/redirect/accounts/sandbox/");
        startWireMock(config);
    }

    public void redirect_mock_of_sandbox_for_anton_brueckner_transactions_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/redirect/transactions/sandbox/");
        startWireMock(config);
    }

    public void embedded_mock_of_sandbox_for_max_musterman_accounts_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/multi-sca/accounts/sandbox/");
        startWireMock(config);
    }

    public void embedded_mock_of_sandbox_for_max_musterman_transactions_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/multi-sca/transactions/sandbox/");
        startWireMock(config);
    }

    @SneakyThrows
    private void startWireMock(WireMockConfiguration config) {
        sandbox = new WireMockServer(config);
        sandbox.start();
        BankProfile bankProfile = bankProfileJpaRepository.findByBankUuid("53c47f54-b9a4-465a-8f77-bc6cd5f0cf46").get();
        bankProfile.setUrl("http://localhost:" + sandbox.port());
        bankProfileJpaRepository.save(bankProfile);

        assertThat(sandbox).isNotNull();
        assertThat(sandbox.isRunning()).isTrue();
    }
}

package de.adorsys.opba.core.protocol.e2e.stages.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs Sandbox as json-backed mock.
 */
@Slf4j
@JGivenStage
public class MockServers extends Stage<MockServers> {

    private static final int PORT = 39393;
    private static final WireMockConfiguration CONFIGURATION = WireMockConfiguration.options()
            .port(PORT)
            .notifier(new Slf4jNotifier(true));

    @ProvidedScenarioState
    private WireMockServer sandbox;

    @AfterScenario
    void stopWireMock() {
        if (null != sandbox) {
            sandbox.shutdown();
        }
    }

    public void redirect_mock_of_sandbox_for_anton_brueckner_accounts_running() {
        WireMockConfiguration config = CONFIGURATION
                .usingFilesUnderClasspath("mockedsandbox/restrecord/redirect/accounts/sandbox/");
        startWireMock(config);
    }

    public void embedded_mock_of_sandbox_for_max_musterman_accounts_running() {
        WireMockConfiguration config = CONFIGURATION
                .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/multi-sca/accounts/sandbox/");
        startWireMock(config);
    }

    private void startWireMock(WireMockConfiguration config) {
        sandbox = new WireMockServer(config);
        sandbox.start();

        assertThat(sandbox).isNotNull();
        assertThat(sandbox.isRunning()).isTrue();
    }
}

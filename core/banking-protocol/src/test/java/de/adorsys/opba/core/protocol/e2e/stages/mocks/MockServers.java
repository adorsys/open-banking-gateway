package de.adorsys.opba.core.protocol.e2e.stages.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs Sandbox as json-backed mock.
 */
@Slf4j
@JGivenStage
public class MockServers extends Stage<MockServers> {

    @ProvidedScenarioState
    private AtomicReference<String> execId = new AtomicReference<>();

    @ProvidedScenarioState
    private WireMockServer sandbox;

    @BeforeStage
    void initWireMock() {
        WireMockConfiguration config = WireMockConfiguration.options()
                .port(39393)
                .usingFilesUnderClasspath("mockedsandbox/restrecord/redirect/accounts/sandbox/")
                .notifier(new Slf4jNotifier(true));

        sandbox = new WireMockServer(config);
        sandbox.start();
    }

    @AfterScenario
    void stopWireMock() {
        if (null != sandbox) {
            sandbox.shutdown();
        }
    }

    public void redirect_sandbox_mock_running() {
        assertThat(sandbox).isNotNull();
        assertThat(sandbox.isRunning()).isTrue();
    }

}

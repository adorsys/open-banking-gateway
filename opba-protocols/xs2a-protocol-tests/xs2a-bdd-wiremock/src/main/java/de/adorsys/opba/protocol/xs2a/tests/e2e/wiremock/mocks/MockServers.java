package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.domain.entity.BankProtocol;
import de.adorsys.opba.db.domain.entity.IgnoreValidationRule;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.IgnoreBankValidationRuleRepository;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.ais.AccountListingService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.CreateAisAccountListConsentService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.authenticate.StartConsentAuthorization;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.authenticate.embedded.Xs2aAuthenticateUserConsentWithPin;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.CommonGivenStages;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Runs Sandbox as json-backed mock.
 */
@Slf4j
@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class MockServers<SELF extends MockServers<SELF>> extends CommonGivenStages<SELF> {

    public static final long AUTH_PROTOCOL_ID = 3L;
    public static final long PROTOCOL_ID = 1L;
    @Autowired
    private BankProfileJpaRepository bankProfileJpaRepository;

    @Autowired
    private IgnoreBankValidationRuleRepository ignoreBankValidationRuleRepository;

    @ProvidedScenarioState
    private WireMockServer sandbox;

    @AfterScenario
    @SneakyThrows
    public void stopWireMock() {
        if (null != sandbox) {
            sandbox.stop();
            sandbox = null;
        }
    }

    public SELF redirect_mock_of_sandbox_for_anton_brueckner_accounts_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/redirect/accounts/sandbox/");
        startWireMock(config);

        return self();
    }

    public SELF redirect_mock_of_sandbox_nopsu_for_anton_brueckner_accounts_running(Path tempDir) {
        URL resource = getClass().getClassLoader().getResource("mockedsandbox/restrecord/redirect/accounts/sandbox/");
        URL resource2 = getClass().getClassLoader().getResource("mockedsandbox/restrecord/redirect/accounts/sandboxnopsu/");
        try {
            FileSystemUtils.copyRecursively(new File(resource.getFile()), tempDir.toFile());
            FileSystemUtils.copyRecursively(new File(resource2.getFile()), tempDir.toFile());
        } catch (IOException e) {
            log.error("files copy to temporary directory error", e);
        }

        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderDirectory(tempDir.toString());
        startWireMock(config);

        return self();
    }

    public SELF redirect_mock_of_sandbox_for_anton_brueckner_transactions_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/redirect/transactions/sandbox/");
        startWireMock(config);

        return self();
    }

    public SELF embedded_mock_of_sandbox_for_max_musterman_accounts_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/multi-sca/accounts/sandbox/");
        startWireMock(config);

        return self();
    }

    public SELF embedded_mock_of_sandbox_for_max_musterman_transactions_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/multi-sca/transactions/sandbox/");
        startWireMock(config);

        return self();
    }

    @SneakyThrows
    private void startWireMock(WireMockConfiguration config) {
        sandbox = new WireMockServer(config);
        sandbox.start();
        BankProfile bankProfile = bankProfileJpaRepository.findByBankUuid("53c47f54-b9a4-465a-8f77-bc6cd5f0cf46").get();
        bankProfile.setUrl("http://localhost:" + sandbox.port());
        bankProfileJpaRepository.save(bankProfile);

        Assertions.assertThat(sandbox).isNotNull();
        Assertions.assertThat(sandbox.isRunning()).isTrue();
    }

    public SELF ignore_validation_rules_table_contains_field_psu_id() {
        IgnoreValidationRule bankValidationRule = IgnoreValidationRule.builder()
                .protocol(BankProtocol.builder().id(PROTOCOL_ID).build())
                .endpointClassCanonicalName(AccountListingService.class.getCanonicalName())
                .forEmbedded(true)
                .forRedirect(true)
                .validationCode(FieldCode.PSU_ID)
                .build();
        ignoreBankValidationRuleRepository.deleteAll();
        ignoreBankValidationRuleRepository.save(bankValidationRule);
        bankValidationRule.setId(null);
        bankValidationRule.setEndpointClassCanonicalName(CreateAisAccountListConsentService.class.getCanonicalName());
        ignoreBankValidationRuleRepository.save(bankValidationRule);
        bankValidationRule.setId(null);
        bankValidationRule.setEndpointClassCanonicalName(StartConsentAuthorization.class.getCanonicalName());
        ignoreBankValidationRuleRepository.save(bankValidationRule);

        bankValidationRule.setId(null);
        bankValidationRule.setProtocol(BankProtocol.builder().id(AUTH_PROTOCOL_ID).build());
        bankValidationRule.setEndpointClassCanonicalName(CreateAisAccountListConsentService.class.getCanonicalName());
        ignoreBankValidationRuleRepository.save(bankValidationRule);
        bankValidationRule.setId(null);
        bankValidationRule.setProtocol(BankProtocol.builder().id(AUTH_PROTOCOL_ID).build());
        bankValidationRule.setEndpointClassCanonicalName(StartConsentAuthorization.class.getCanonicalName());
        ignoreBankValidationRuleRepository.save(bankValidationRule);

        bankValidationRule.setId(null);
        bankValidationRule.setProtocol(BankProtocol.builder().id(AUTH_PROTOCOL_ID).build());
        bankValidationRule.setEndpointClassCanonicalName(Xs2aAuthenticateUserConsentWithPin.class.getCanonicalName());
        ignoreBankValidationRuleRepository.save(bankValidationRule);

        return self();
    }
}

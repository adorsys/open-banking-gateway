package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.db.domain.entity.BankAction;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.domain.entity.IgnoreValidationRule;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.IgnoreValidationRuleRepository;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.ais.Xs2aAccountListingService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.StartConsentAuthorization;
import de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.embedded.Xs2aAisAuthenticateUserConsentWithPin;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.CreateAisAccountListConsentService;
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

    public static final long AUTH_ACTION_ID = 3L;
    public static final long ACTION_ID = 1L;
    @Autowired
    private BankProfileJpaRepository bankProfileJpaRepository;

    @Autowired
    @ProvidedScenarioState
    private IgnoreValidationRuleRepository ignoreValidationRuleRepository;

    @ProvidedScenarioState
    private WireMockServer sandbox;

    @AfterScenario
    @SneakyThrows
    public void stopWireMock() {
        if (null != sandbox) {
            sandbox.stop();
            sandbox = null;
        }

        if (null != ignoreValidationRuleRepository) {
            ignoreValidationRuleRepository.deleteAll();
        }
    }

    public SELF redirect_mock_of_sandbox_for_anton_brueckner_accounts_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/redirect/accounts/sandbox/");
        startWireMock(config);

        return self();
    }

    public SELF redirect_mock_of_sandbox_for_anton_brueckner_accounts_running_for_non_happy_path() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                                               .usingFilesUnderClasspath("mockedsandbox/restrecord-nonhappy/redirect/accounts/sandbox/");
        startWireMock(config);

        return self();
    }

    public SELF redirect_mock_of_sandbox_nopsu_for_anton_brueckner_accounts_running_for_non_happy_path(Path tempDir) {
        URL resource = getClass().getClassLoader().getResource("mockedsandbox/restrecord-nonhappy/redirect/accounts/sandbox/");
        URL resource2 = getClass().getClassLoader().getResource("mockedsandbox/restrecord-nonhappy/redirect/accounts/sandboxnopsu/");
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

    public SELF redirect_mock_of_sandbox_for_anton_brueckner_payments_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                                               .usingFilesUnderClasspath("mockedsandbox/restrecord/redirect/payments/sandbox/");
        startWireMock(config);

        return self();
    }

    public SELF embedded_mock_of_sandbox_for_max_musterman_accounts_running_with_balance_for_happy_path(Path tempDir) {
        URL resource1 = getClass().getClassLoader().getResource("mockedsandbox/restrecord/embedded/multi-sca/accounts/sandbox/");
        URL resource2 = getClass().getClassLoader().getResource("mockedsandbox/restrecord/embedded/multi-sca/accounts-with-balance/sandbox/");
        try {
            FileSystemUtils.copyRecursively(new File(resource1.getFile()), tempDir.toFile());
            FileSystemUtils.copyRecursively(new File(resource2.getFile()), tempDir.toFile());
        } catch (IOException e) {
            log.error("files copy to temporary directory error", e);
        }

        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
            .usingFilesUnderDirectory(tempDir.toString());
        startWireMock(config);

        return self();
    }

    public SELF embedded_mock_of_sandbox_for_max_musterman_accounts_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/multi-sca/accounts/sandbox/");
        startWireMock(config);

        return self();
    }

    public SELF embedded_mock_of_sandbox_for_max_musterman_zero_sca_accounts_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                                               .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/zero-sca/accounts/sandbox/");
        startWireMock(config);

        return self();
    }

    public SELF embedded_mock_of_sandbox_for_max_musterman_payments_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                                               .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/multi-sca/payments/sandbox/");
        startWireMock(config);

        return self();
    }

    public SELF embedded_mock_of_sandbox_for_max_musterman_zero_sca_payments_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                                               .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/zero-sca/payments/sandbox/");
        startWireMock(config);

        return self();
    }

    // Stress tests can't use WireMock state without making them complicated
    public SELF embedded_mock_of_sandbox_for_max_musterman_accounts_running_stateless() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/multi-sca/stateless/accounts/sandbox/");
        startWireMock(config);

        return self();
    }

    public SELF embedded_mock_of_sandbox_for_max_musterman_accounts_running_for_non_happy_path() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                                               .usingFilesUnderClasspath("mockedsandbox/restrecord-nonhappy/embedded/multi-sca/accounts/sandbox/");
        startWireMock(config);

        return self();
    }

    // Stress tests can't use WireMock state without making them complicated
    public SELF embedded_mock_of_sandbox_for_max_musterman_transactions_running_stateless() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/multi-sca/stateless/transactions/sandbox/");
        startWireMock(config);

        return self();
    }

    public SELF embedded_mock_of_sandbox_for_max_musterman_transactions_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/multi-sca/transactions/sandbox/");
        startWireMock(config);

        return self();
    }

    public SELF embedded_mock_of_sandbox_for_max_musterman_zero_sca_transactions_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                                               .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/zero-sca/transactions/sandbox/");
        startWireMock(config);

        return self();
    }

    public SELF embedded_mock_of_sandbox_for_max_musterman_transactions_running_for_non_happy_path() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                                               .usingFilesUnderClasspath("mockedsandbox/restrecord-nonhappy/embedded/multi-sca/transactions/sandbox/");
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
                .action(BankAction.builder().id(ACTION_ID).build())
                .endpointClassCanonicalName(Xs2aAccountListingService.class.getCanonicalName())
                .forEmbedded(true)
                .forRedirect(true)
                .validationCode(FieldCode.PSU_ID)
                .build();
        ignoreValidationRuleRepository.deleteAll();
        ignoreValidationRuleRepository.save(bankValidationRule);
        bankValidationRule.setId(null);
        bankValidationRule.setEndpointClassCanonicalName(CreateAisAccountListConsentService.class.getCanonicalName());
        ignoreValidationRuleRepository.save(bankValidationRule);
        bankValidationRule.setId(null);
        bankValidationRule.setEndpointClassCanonicalName(StartConsentAuthorization.class.getCanonicalName());
        ignoreValidationRuleRepository.save(bankValidationRule);

        bankValidationRule.setId(null);
        bankValidationRule.setAction(BankAction.builder().id(AUTH_ACTION_ID).build());
        bankValidationRule.setEndpointClassCanonicalName(CreateAisAccountListConsentService.class.getCanonicalName());
        ignoreValidationRuleRepository.save(bankValidationRule);
        bankValidationRule.setId(null);
        bankValidationRule.setAction(BankAction.builder().id(AUTH_ACTION_ID).build());
        bankValidationRule.setEndpointClassCanonicalName(StartConsentAuthorization.class.getCanonicalName());
        ignoreValidationRuleRepository.save(bankValidationRule);

        bankValidationRule.setId(null);
        bankValidationRule.setAction(BankAction.builder().id(AUTH_ACTION_ID).build());
        bankValidationRule.setEndpointClassCanonicalName(Xs2aAisAuthenticateUserConsentWithPin.class.getCanonicalName());
        ignoreValidationRuleRepository.save(bankValidationRule);

        return self();
    }

    public SELF ignore_validation_rules_table_ignore_missing_ip_address() {
        IgnoreValidationRule bankValidationRuleForInit = IgnoreValidationRule.builder()
                                                          .action(BankAction.builder().id(ACTION_ID).build())
                                                          .forEmbedded(true)
                                                          .forRedirect(true)
                                                          .validationCode(FieldCode.PSU_IP_ADDRESS)
                                                          .build();

        IgnoreValidationRule bankValidationRuleForAuth = IgnoreValidationRule.builder()
                                                                 .action(BankAction.builder().id(AUTH_ACTION_ID).build())
                                                                 .forEmbedded(true)
                                                                 .forRedirect(true)
                                                                 .validationCode(FieldCode.PSU_IP_ADDRESS)
                                                                 .build();

        ignoreValidationRuleRepository.deleteAll();
        ignoreValidationRuleRepository.save(bankValidationRuleForInit);
        ignoreValidationRuleRepository.save(bankValidationRuleForAuth);

        return self();
    }

    public SELF ignore_validation_rules_table_do_not_ignore_missing_psu_ip_port() {
        IgnoreValidationRule bankValidationRuleForInit = IgnoreValidationRule.builder()
                                                          .action(BankAction.builder().id(ACTION_ID).build())
                                                          .forEmbedded(false)
                                                          .forRedirect(false)
                                                          .validationCode(FieldCode.PSU_IP_PORT)
                                                          .build();

        IgnoreValidationRule bankValidationRuleForAuth = IgnoreValidationRule.builder()
                                                                 .action(BankAction.builder().id(AUTH_ACTION_ID).build())
                                                                 .forEmbedded(false)
                                                                 .forRedirect(false)
                                                                 .validationCode(FieldCode.PSU_IP_PORT)
                                                                 .build();
        ignoreValidationRuleRepository.deleteAll();
        ignoreValidationRuleRepository.save(bankValidationRuleForInit);
        ignoreValidationRuleRepository.save(bankValidationRuleForAuth);

        return self();
    }

    public SELF ignore_validation_rules_table_ignore_missing_psu_ip_port() {
        IgnoreValidationRule bankValidationRuleForInit = IgnoreValidationRule.builder()
                                                          .action(BankAction.builder().id(ACTION_ID).build())
                                                          .forEmbedded(true)
                                                          .forRedirect(true)
                                                          .validationCode(FieldCode.PSU_IP_PORT)
                                                          .build();

        IgnoreValidationRule bankValidationRuleForAuth = IgnoreValidationRule.builder()
                                                                 .action(BankAction.builder().id(AUTH_ACTION_ID).build())
                                                                 .forEmbedded(true)
                                                                 .forRedirect(true)
                                                                 .validationCode(FieldCode.PSU_IP_PORT)
                                                                 .build();

        ignoreValidationRuleRepository.deleteAll();
        ignoreValidationRuleRepository.save(bankValidationRuleForInit);
        ignoreValidationRuleRepository.save(bankValidationRuleForAuth);

        return self();
    }
}

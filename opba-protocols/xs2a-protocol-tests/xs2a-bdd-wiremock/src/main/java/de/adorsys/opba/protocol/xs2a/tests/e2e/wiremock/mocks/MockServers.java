package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.google.common.io.Resources;
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
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Runs Sandbox as json-backed mock.
 */
@Slf4j
@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class MockServers<SELF extends MockServers<SELF>> extends CommonGivenStages<SELF> {

    public static final long AUTH_ACTION_ID = 3L;
    public static final long ACTION_ID = 1L;
    public static final String DKB_BANK_ID = "335562a2-26e2-4105-b31e-08de285234e0";

    @Autowired
    private BankProfileJpaRepository bankProfileJpaRepository;

    @Autowired
    @ProvidedScenarioState
    private IgnoreValidationRuleRepository ignoreValidationRuleRepository;

    @ProvidedScenarioState
    private WireMockServer sandbox;

    Consumer<BankProfile> defaultbankProfileConfigurer = it -> {
        it.setUrl("http://localhost:" + sandbox.port());
        it.setIdpUrl("http://localhost:" + sandbox.port() + "/oauth/authorization-server");
    };

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

    public SELF oauth2_prestep_mock_of_sandbox_for_anton_brueckner_accounts_running(Path tempDir) {
        mergeWireMockFixtures(
                tempDir,
                "mockedsandbox/restrecord/oauth2/prestep/accounts/results-oauth2",
                "mockedsandbox/restrecord/oauth2/prestep/accounts/results-xs2a"
        );

        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath(tempDir.toAbsolutePath().toString())
                .extensions(new ResponseTemplateTransformer(false));
        startWireMock(config);

        return self();
    }

    public SELF oauth2_integrated_mock_of_sandbox_for_anton_brueckner_accounts_running(Path tempDir) {
        mergeWireMockFixtures(
                tempDir,
                "mockedsandbox/restrecord/oauth2/integrated/accounts/results-oauth2",
                "mockedsandbox/restrecord/oauth2/integrated/accounts/results-xs2a"
        );

        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath(tempDir.toAbsolutePath().toString())
                .extensions(new ResponseTemplateTransformer(false));
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
        mergeWireMockFixtures(tempDir, resource, resource2);

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

    public SELF oauth2_prestep_mock_of_sandbox_for_anton_brueckner_payments_running(Path tempDir) {
        mergeWireMockFixtures(
                tempDir,
                "mockedsandbox/restrecord/oauth2/prestep/payments/results-oauth2",
                "mockedsandbox/restrecord/oauth2/prestep/payments/results-xs2a"
        );

        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath(tempDir.toAbsolutePath().toString())
                .extensions(new ResponseTemplateTransformer(false));
        startWireMock(config);

        return self();
    }

    public SELF oauth2_integrated_mock_of_sandbox_for_anton_brueckner_payments_running(Path tempDir) {
        mergeWireMockFixtures(
                tempDir,
                "mockedsandbox/restrecord/oauth2/integrated/payments/results-oauth2",
                "mockedsandbox/restrecord/oauth2/integrated/payments/results-xs2a"
        );

        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath(tempDir.toAbsolutePath().toString())
                .extensions(new ResponseTemplateTransformer(false));
        startWireMock(config);

        return self();
    }

    public SELF embedded_mock_of_sandbox_for_max_musterman_accounts_running_with_balance_for_happy_path(Path tempDir) {
        URL resource1 = getClass().getClassLoader().getResource("mockedsandbox/restrecord/embedded/multi-sca/accounts/sandbox/");
        URL resource2 = getClass().getClassLoader().getResource("mockedsandbox/restrecord/embedded/multi-sca/accounts-with-balance/sandbox/");
        mergeWireMockFixtures(tempDir, resource1, resource2);

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

    public SELF embedded_pre_step_mock_of_dkb_sandbox_for_max_musterman_accounts_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/pre-step/accounts/");
        config.notifier(new Slf4jNotifier(true));
        startWireMock(config, DKB_BANK_ID, defaultbankProfileConfigurer);
        return self();
    }
    public SELF embedded_mock_of_postbank_for_anton_brueckner_accounts_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/multi-sca/accounts/postbank/");
        config.notifier(new Slf4jNotifier(true));

        startWireMock(config, "01aa84f2-25c0-4e02-8065-c401657e3fb0", it -> it.setUrl("http://localhost:" + sandbox.port() + "/{Service Group}/DE/Postbank"));
        return self();
    }
    public SELF embedded_mock_of_sandbox_for_max_musterman_zero_sca_accounts_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                                               .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/zero-sca/accounts/sandbox/");
        startWireMock(config);

        return self();
    }

    public SELF embedded_pre_step_mock_of_dkb_sandbox_for_max_musterman_payments_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
            .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/pre-step/payments/");
        startWireMock(config, DKB_BANK_ID, defaultbankProfileConfigurer);

        return self();
    }

    public SELF embedded_pre_step_mock_of_postbank_sandbox_for_max_musterman_payments_running() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort()
                .usingFilesUnderClasspath("mockedsandbox/restrecord/embedded/multi-sca/payments/postbank/");
        config.notifier(new Slf4jNotifier(true));

        startWireMock(config, "01aa84f2-25c0-4e02-8065-c401657e3fb0", it -> it.setUrl("http://localhost:" + sandbox.port() + "/{Service Group}/DE/Postbank"));
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

        startWireMock(config, "adadadad-4000-0000-0000-b0b0b0b0b0b0", defaultbankProfileConfigurer);
    }

    @SneakyThrows
    private void startWireMock(WireMockConfiguration config, String bankId, Consumer<BankProfile> bankProfileConfigurer) {
        sandbox = new WireMockServer(config);
        sandbox.start();
        var bankProfiles = bankProfileJpaRepository.findByBankUuid(UUID.fromString(bankId));
        bankProfiles.forEach(bankProfileConfigurer);
        bankProfileJpaRepository.saveAll(bankProfiles);

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

    private void mergeWireMockFixtures(Path destination, String... resources) {
        mergeWireMockFixtures(destination, Arrays.stream(resources).map(Resources::getResource).toArray(URL[]::new));
    }

    private void mergeWireMockFixtures(Path destination, URL... resources) {
        try {
            for (URL resource : resources) {
                FileSystemUtils.copyRecursively(new File(resource.getFile()), destination.toFile());
            }
        } catch (IOException ex) {
            log.error("files copy to temporary directory error", ex);
            throw new RuntimeException(ex);
        }
    }
}

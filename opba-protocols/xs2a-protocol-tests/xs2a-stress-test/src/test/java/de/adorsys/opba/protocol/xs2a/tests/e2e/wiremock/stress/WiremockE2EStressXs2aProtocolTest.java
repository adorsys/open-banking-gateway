package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.stress;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolConfiguration;
import de.adorsys.opba.protocol.xs2a.tests.e2e.JGivenConfig;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationResult;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.MockServers;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockAccountInformationRequest;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.Xs2aProtocolApplication;
import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.domain.TestMappings;
import org.jsmart.zerocode.jupiter.extension.ParallelLoadExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static de.adorsys.opba.protocol.xs2a.tests.Const.ENABLE_HEAVY_TESTS;
import static de.adorsys.opba.protocol.xs2a.tests.Const.TRUE_BOOL;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.MOCKED_SANDBOX;
import static de.adorsys.opba.protocol.xs2a.tests.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@EnabledIfEnvironmentVariable(named = ENABLE_HEAVY_TESTS, matches = TRUE_BOOL)
@ExtendWith({WiremockE2EStressXs2aProtocolTest.RestAssuredConfigurer.class, ParallelLoadExtension.class})
/*
 * Set to use Xs2aProtocol, not Sandbox protocol for transaction listing:
 */
@Sql(statements = "UPDATE opb_bank_protocol SET protocol_bean_name = 'xs2aListTransactions' WHERE protocol_bean_name = 'xs2aSandboxListTransactions'")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {Xs2aProtocolApplication.class, JGivenConfig.class}, webEnvironment = RANDOM_PORT)
@ActiveProfiles(profiles = {ONE_TIME_POSTGRES_RAMFS, MOCKED_SANDBOX})
class WiremockE2EStressXs2aProtocolTest extends SpringScenarioTest<MockServers, WiremockAccountInformationRequest<? extends WiremockAccountInformationRequest<?>>, AccountInformationResult> {

    @LocalServerPort
    private int port;

    @Autowired
    private ProtocolConfiguration configuration;

    // See https://github.com/spring-projects/spring-boot/issues/14879 for the 'why setting port'
    void beforeEach() {
        ProtocolConfiguration.Redirect.Consent consent = configuration.getRedirect().getConsentAccounts();
        consent.setOk(consent.getOk().replaceAll("localhost:\\d+", "localhost:" + port));
        consent.setNok(consent.getNok().replaceAll("localhost:\\d+", "localhost:" + port));
    }

    // JGivenConfig doesn't seem to be applied
    @AfterEach
    void afterEach() {
        given().stopWireMock();
    }


    @Test
    @LoadWith("load_generation.properties")
    @TestMappings({
        @TestMapping(testClass = E2EStress.class, testMethod = "embeddedAccountList")
    })
    void testEmbeddedAccountListConcurrent() {
        // NOP
    }

    @Test
    @LoadWith("load_generation.properties")
    @TestMappings({
        @TestMapping(testClass = E2EStress.class, testMethod = "embeddedTransactionList")
    })
    void testEmbeddedTransactionListConcurrent() {
        // NOP
    }

    /**
     * Unfortunately, {@link ParallelLoadExtension} prevents @BeforeEach from executing. So doing this hack in order to
     * setup RestAssured and other stuff.
     */
    public static class RestAssuredConfigurer implements BeforeEachCallback {

        @Override
        public void beforeEach(ExtensionContext context) {
            WiremockE2EStressXs2aProtocolTest instance =
                (WiremockE2EStressXs2aProtocolTest) context.getTestInstance().get();

            instance.beforeEach();
            if ("testEmbeddedAccountListConcurrent".equals(context.getTestMethod().get().getName())) {
                instance.given()
                    .embedded_mock_of_sandbox_for_max_musterman_accounts_running()
                    .rest_assured_points_to_opba_server();

                // register FinTech to avoid normal concurrent failures - if FinTech doesn't exist some requests
                // trying to register it can fail because only one can succeed
                instance.when().fintech_calls_list_accounts_for_anton_brueckner();
            }

            if ("testEmbeddedTransactionListConcurrent".equals(context.getTestMethod().get().getName())) {
                instance.given()
                    .embedded_mock_of_sandbox_for_max_musterman_transactions_running()
                    .rest_assured_points_to_opba_server();

                // register FinTech to avoid normal concurrent failures - if FinTech doesn't exist some requests
                // trying to register it can fail because only one can succeed
                instance.when().fintech_calls_list_accounts_for_anton_brueckner();
            }
        }
    }
}

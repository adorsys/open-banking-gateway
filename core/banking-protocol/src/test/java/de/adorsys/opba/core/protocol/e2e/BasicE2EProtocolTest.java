package de.adorsys.opba.core.protocol.e2e;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.StubImport;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import de.adorsys.opba.core.protocol.BaseMockitoTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.lanwen.wiremock.config.WiremockConfigFactory;
import ru.lanwen.wiremock.config.WiremockCustomizer;
import ru.lanwen.wiremock.ext.WiremockResolver;
import ru.lanwen.wiremock.ext.WiremockUriResolver;

import static de.adorsys.opba.core.protocol.TestProfiles.MOCKED_SANDBOX;

/**
 * Happy-path test that uses wiremock-stubbed request-responses to drive banking-protocol.
 */
@SpringBootTest
@ExtendWith({
        WiremockResolver.class,
        WiremockUriResolver.class
})
@ActiveProfiles(profiles = {MOCKED_SANDBOX})
class BasicE2EProtocolTest extends BaseMockitoTest {

    @Test
    @SneakyThrows
    void testAccountsListWithConsentUsingRedirect(
            @WiremockResolver.Wiremock(factory = Configurer.class, customizer = Customizer.class)
                    WireMockServer server) {
    }

    @Test
    void testTransactionsListWithConsentUsingRedirect() {
    }

    @Test
    void testAccountsListWithConsentUsingEmbedded() {
    }

    @Test
    void testTransactionsListWithConsentUsingEmbedded() {
    }

    public static class Customizer implements WiremockCustomizer {

        @Override
        public void customize(WireMockServer server) {
            server.importStubs(StubImport.stubImport()
                    .stub(StubMapping.buildFrom("mockedsandbox/restrecord/redirect/accounts/sandbox/"))
                    .build()
            );
        }
    }

    public static class Configurer implements WiremockConfigFactory {

        @Override
        public WireMockConfiguration create() {
            return WireMockConfiguration.options()
                    .port(39393)
                    .notifier(new Slf4jNotifier(true));
        }
    }
}

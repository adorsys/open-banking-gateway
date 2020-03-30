package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.wiremock.config;

import de.adorsys.opba.consentapi.config.EnableConsentApi;
import de.adorsys.opba.db.config.EnableBankingPersistence;
import de.adorsys.opba.protocol.xs2a.EnableXs2aSandboxProtocol;
import de.adorsys.opba.tppbankingapi.config.EnableBankingApi;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableConsentApi
@EnableBankingApi
@EnableXs2aSandboxProtocol
@EnableBankingPersistence
@SpringBootApplication
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Spring entry point
public class Xs2aSandboxProtocolApplication {
}

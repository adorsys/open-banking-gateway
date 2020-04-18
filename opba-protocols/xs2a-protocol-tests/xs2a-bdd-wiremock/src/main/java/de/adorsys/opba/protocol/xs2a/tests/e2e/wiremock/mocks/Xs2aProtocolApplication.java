package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks;

import de.adorsys.opba.api.security.EnableTokenBasedApiSecurity;
import de.adorsys.opba.consentapi.config.EnableConsentApi;
import de.adorsys.opba.db.config.EnableBankingPersistence;
import de.adorsys.opba.protocol.xs2a.EnableXs2aProtocol;
import de.adorsys.opba.tppauthapi.config.EnableTppAuthApi;
import de.adorsys.opba.tppbankingapi.config.EnableBankingApi;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableTppAuthApi
@EnableConsentApi
@EnableBankingApi
@EnableXs2aProtocol
@EnableBankingPersistence
@EnableTokenBasedApiSecurity
@SpringBootApplication
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Spring entry point
public class Xs2aProtocolApplication {
}

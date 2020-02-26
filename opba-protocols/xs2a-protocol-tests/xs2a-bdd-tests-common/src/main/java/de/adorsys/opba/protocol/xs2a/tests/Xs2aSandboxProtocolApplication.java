package de.adorsys.opba.protocol.xs2a.tests;

import de.adorsys.opba.db.config.EnableBankingPersistence;
import de.adorsys.opba.protocol.xs2a.EnableXs2aSandboxProtocol;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableXs2aSandboxProtocol
@EnableBankingPersistence
@SpringBootApplication
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Spring entry point
public class Xs2aSandboxProtocolApplication {
}

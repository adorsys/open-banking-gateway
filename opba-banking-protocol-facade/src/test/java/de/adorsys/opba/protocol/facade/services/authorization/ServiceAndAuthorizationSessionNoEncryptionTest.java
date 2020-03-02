package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.protocol.facade.config.ApplicationTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test, no-encryption")
@SpringBootTest(classes = ApplicationTest.class)
class ServiceAndAuthorizationSessionNoEncryptionTest extends AbstractServiceSessionTest {
    private static final String NOOP_ALGO = "NOOP";

    @Override
    String getAlgorithm() {
        return NOOP_ALGO;
    }
}
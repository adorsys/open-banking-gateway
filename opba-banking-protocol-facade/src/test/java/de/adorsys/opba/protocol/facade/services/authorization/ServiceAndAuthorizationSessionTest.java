package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.protocol.facade.config.ApplicationTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = ApplicationTest.class)
class ServiceAndAuthorizationSessionTest extends AbstractServiceSessionTest {
}
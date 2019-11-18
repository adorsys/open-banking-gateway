package de.adorsys.openbankinggateway;

import de.adorsys.ledgers.gatway.LedgersXs2aGatewayApplication;
import de.adorsys.psd2.aspsp.profile.AspspProfileApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // Performance optimization
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = {AspspProfileApplication.class, LedgersXs2aGatewayApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public abstract class WithSandboxSpringBootTest extends BaseMockitoTest {
}

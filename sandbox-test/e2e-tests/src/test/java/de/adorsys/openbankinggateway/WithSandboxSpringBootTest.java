package de.adorsys.openbankinggateway;

/*@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // Performance optimization
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = {AspspProfileApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)*/
public abstract class WithSandboxSpringBootTest extends BaseMockitoTest {
}

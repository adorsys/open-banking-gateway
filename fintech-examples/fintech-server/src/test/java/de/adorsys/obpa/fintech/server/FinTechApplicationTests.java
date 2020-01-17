package de.adorsys.obpa.fintech.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("application-test-one-time-postgres-ramfs")
@AutoConfigureMockMvc
class FinTechApplicationTests {

	@Test
	void contextLoads() {
	}

}

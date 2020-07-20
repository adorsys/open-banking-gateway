package de.adorsys.opba.fintech.server;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.config.ServiceAccountsConfig;
import de.adorsys.opba.fintech.impl.config.UserRegistrationConfig;
import de.adorsys.opba.fintech.impl.service.ServiceAccountsOper;
import de.adorsys.opba.fintech.server.config.TestConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static de.adorsys.opba.fintech.impl.config.UserRegistrationConfig.SecurityState.DENY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@EnableFinTechImplConfig
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ActiveProfiles("service-account")
public class AuthorizationServiceAccountApiTest extends FinTechApiBaseTest {

    private static final String SERVICE_LOGIN = "testServiceAccount";
    private static final String SERVICE_PASSWORD = "changeme-test-service-password";
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ServiceAccountsOper oper;

    @Autowired
    private ServiceAccountsConfig accountsConfig;

    @Autowired
    private UserRegistrationConfig userRegistrationConfig;

    @BeforeEach
    void sanityCheck() {
        assertThat(userRegistrationConfig.getSimple()).isEqualTo(DENY);
    }

    @Test
    @SneakyThrows
    public void loginPostForActiveIsOk() {
        MvcResult result = plainAuth(mvc, SERVICE_LOGIN, SERVICE_PASSWORD);
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @SneakyThrows
    public void loginPostForInactiveUserFails() {
        accountsConfig.getAccounts().clear();
        oper.createOrActivateOrDeactivateServiceAccounts();

        MvcResult result = plainAuth(mvc, SERVICE_LOGIN, SERVICE_PASSWORD);
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}

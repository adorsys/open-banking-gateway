package de.adorsys.opba.fintech.server;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.config.UserRegistrationConfig;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import de.adorsys.opba.fintech.server.config.TestConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@EnableFinTechImplConfig
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class AuthorizationApiTest extends FinTechApiBaseTest {

    private static final String LOGIN = "peter";
    private static final String PASSWORD = "1234";
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository users;

    @Autowired
    private UserRegistrationConfig userRegistrationConfig;

    @Test
    @SneakyThrows
    public void loginPostForActiveServiceAccountIsOk() {
        MvcResult result = plainAuth(mvc, LOGIN, PASSWORD);
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @SneakyThrows
    public void autoUserRegistrationFailsIfDisabled() {
        userRegistrationConfig.setSimple(UserRegistrationConfig.SecurityState.DENY);

        MvcResult result = plainAuth(mvc, LOGIN, PASSWORD);

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @SneakyThrows
    public void loginPostForInactivedServiceAccountUserFails() {
        MvcResult result = plainAuth(mvc, "peter", "1234");
        UserEntity user = users.findById("peter").get();
        assertThat(user.isServiceAccount()).isFalse();
        assertThat(user.isActive()).isTrue();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        user.setActive(false);
        users.save(user);

        result = plainAuth(mvc, "peter", "1234");
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}

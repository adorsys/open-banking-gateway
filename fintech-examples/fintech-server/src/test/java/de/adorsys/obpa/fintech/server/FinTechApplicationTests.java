package de.adorsys.obpa.fintech.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.obpa.fintech.server.bankmocks.MockedTppBankSearchController;
import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@EnableFinTechImplConfig
@ComponentScan("de.adorsys.obpa.fintech.server.bankmocks")
class FinTechApplicationTests {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    @Autowired
    MockedTppBankSearchController c;

    @Autowired
    protected MockMvc mvc;

	@Test
    @SneakyThrows
    public void loginPostOk() {
        auth("peter", "1234");
    }

    @SneakyThrows
    public String auth(String username, String password) {
        LoginBody loginBody = new LoginBody(username, password);
        MvcResult result = this.mvc
                .perform(MockMvcRequestBuilders.post("/v1/login")
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .content(jsonMapper.writeValueAsString(loginBody))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists("X-XSRF-TOKEN"))
                .andReturn();
        return result.getResponse().getHeader("X-XSRF-TOKEN");
    }

    @Test
    @SneakyThrows
    public void loginPostUnAuthorized() {
        LoginBody loginBody = new LoginBody("peter", "12345");
        this.mvc
                .perform(MockMvcRequestBuilders.post("/v1/login")
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .content(jsonMapper.writeValueAsString(loginBody))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.header().doesNotExist("X-XSRF-TOKEN"));
    }

    @Test
    @SneakyThrows
    public void bankSearchAuthorized() {

	LoginBody loginBody = new LoginBody("peter", "1234");
        String xsrfToken = auth(loginBody.username, loginBody.password);
        this.mvc
                .perform(MockMvcRequestBuilders.get("/v1/search/bankSearch")
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .header("X-XSRF-TOKEN", xsrfToken)
                        .param("keyword", "affe")
                        .param("start", "0")
                        .param("max", "2"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @AllArgsConstructor
    @Getter
    private static class LoginBody {
        String username;
        String password;
    }

}

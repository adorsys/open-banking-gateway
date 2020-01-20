package de.adorsys.obpa.fintech.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@EnableFinTechImplConfig
class FinTechApplicationTests {

	private static final ObjectMapper jsonMapper = new ObjectMapper();

	@Autowired
	protected MockMvc mvc;

	@Test
	void contextLoads() {
		loginPost("peter", "1234");
	}

	@SneakyThrows
	MvcResult loginPost(String user, String password) {
		LoginBody loginBody = new LoginBody(user, password);
		return this.mvc
				.perform(post("/v1/login").
						header("X-Request-ID", UUID.randomUUID().toString()).
						content(jsonMapper.writeValueAsString(loginBody)).
						contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				// .andExpect(MockMvcResultMatchers.cookie().value("fintech", "affe"))
				.andReturn();
	}

	@AllArgsConstructor
	@Getter
	private static class LoginBody {
		String username;
		String password;
	}

}

package de.adorsys.opba.fintech.server;

import com.google.gson.Gson;
import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.config.FinTechImplConfig;
import de.adorsys.opba.tpp.bankserach.api.model.generated.BankProfileResponse;
import de.adorsys.opba.tpp.bankserach.api.model.generated.BankSearchResponse;
import de.adorsys.opba.tpp.bankserach.api.resource.generated.TppBankSearchApi;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FinTechServerTests.TestConfig.class)
@AutoConfigureMockMvc
@Slf4j
class FinTechServerTests {
    private static final String FIN_TECH_AUTH_URL = "/v1/login";
    private static final String FIN_TECH_BANK_SEARCH_URL = "/v1/search/bankSearch";
    private static final String FIN_TECH_BANK_PROFILE_URL = "/v1/search/bankProfile";

    private static final String BANK_SEARCH_RESPONSE_PREFIX = "TPP_BankSearchResponse";
    private static final String BANK_PROFILE_RESPONSE_PREFIX = "TPP_BankProfileResponse";
    private static final String POSTFIX = ".json";

    private static final Gson gson = new Gson();

    @MockBean
    private TestConfig.MockFeign mockFeign;

    @MockBean
    private TppBankSearchApi mockedTppBankSearchApi;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Autowired
    protected MockMvc mvc;

    @Test
    @SneakyThrows
    public void loginPostOk() {
        authOk("peter", "1234");
    }

    @Test
    @SneakyThrows
    public void loginPostUnAuthorized() {
        MvcResult result = plainauth("peter", "12345");
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
        assertNull(result.getResponse().getCookie("XSRF-TOKEN"));
    }

    @Test
    @SneakyThrows
    public void bankSearchAuthorized() {
        final String keyword = "affe";
        final Integer start = 1;
        final Integer max = 2;

        when(mockFeign.bankSearchGET(any(), any(), eq(keyword), eq(start), eq(max)))
                .thenReturn(ResponseEntity.ok(gson.fromJson(readFile(getFilenameBankSearch(keyword, start, max)), BankSearchResponse.class)));

        LoginBody loginBody = new LoginBody("peter", "1234");
        String xsrfToken = authOk(loginBody.username, loginBody.password);
        bankSearchOk(keyword, start, max, xsrfToken);
    }

    @Test
    @SneakyThrows
    public void bankSearchUnAuthorized() {
        MvcResult result = plainBankSearch("affe", 0, 2, "invalidToken");
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
    }

    /**
     * This test does
     * authorization
     * bankSearch
     * bankProfile
     */
    @Test
    @SneakyThrows
    public void bankProfileAuthorized() {
        String xsrfToken = null;
        {
            final String user = "peter";
            final String password = "1234";
            log.info("DO Authorization ({}, {}) ==============================", user, password);
            LoginBody loginBody = new LoginBody(user, password);
            xsrfToken = authOk(loginBody.username, loginBody.password);
        }

        String bankUUID = null;
        {
            final String keyword = "affe";
            final Integer start = 1;
            final Integer max = 2;
            log.info("DO Bank Search ({}, {}, {}) ==============================", keyword, start, max);

            when(mockFeign.bankSearchGET(any(), any(), eq(keyword), eq(start), eq(max)))
                    .thenReturn(ResponseEntity.ok(gson.fromJson(readFile(getFilenameBankSearch(keyword, start, max)), BankSearchResponse.class)));

            bankUUID = bankSearchOk(keyword, start, max, xsrfToken);
        }

        List<String> services = null;
        {
            log.info("DO Bank Profile ({}) ============================== ", bankUUID);
            when(mockFeign.bankProfileGET(any(), any(), eq(bankUUID)))
                    .thenReturn(ResponseEntity.ok(gson.fromJson(readFile(getFilenameBankProfile(bankUUID)), BankProfileResponse.class)));

            services = bankProfile(xsrfToken, bankUUID);
            assertTrue(services.containsAll(Arrays.asList(new String[]{"List accounts", "List transactions", "Initiate payment"})));
        }
        log.info(" i have done it");
    }

    /**
     * @param xsrfToken
     * @param bankUUID
     * @return List of Services of Bank
     */
    @SneakyThrows
    private List<String> bankProfile(String xsrfToken, String bankUUID) {
        MvcResult mvcResult = this.mvc
                .perform(get(FIN_TECH_BANK_PROFILE_URL)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .header("X-XSRF-TOKEN", xsrfToken)
                        .param("bankId", bankUUID))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<String> result = new ArrayList<>();
        JSONArray jsonArray = new JSONObject(mvcResult.getResponse().getContentAsString()).getJSONObject("bankProfile").getJSONArray("services");
        for (int i = 0; i < jsonArray.length(); i++) {
            result.add(jsonArray.getString(i));
        }
        return result;
    }

    /**
     * @param username
     * @param password
     * @return XSRF Token
     */
    @SneakyThrows
    private String authOk(String username, String password) {
        MvcResult result = plainauth(username, password);
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(result.getResponse().getCookie("XSRF-TOKEN"));
        return result.getResponse().getCookie("XSRF-TOKEN").getValue();
    }

    @SneakyThrows
    private MvcResult plainauth(String username, String password) {
        LoginBody loginBody = new LoginBody(username, password);
        return this.mvc
                .perform(post(FIN_TECH_AUTH_URL)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .content(gson.toJson(loginBody))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
    }

    /**
     * @param keyword
     * @param start
     * @param max
     * @param xsrfToken
     * @return first BankUUID of found list
     */
    @SneakyThrows
    private String bankSearchOk(String keyword, Integer start, Integer max, String xsrfToken) {
        MvcResult result = plainBankSearch(keyword, start, max, xsrfToken);
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        return new JSONObject(result.getResponse().getContentAsString()).getJSONArray("bankDescriptor").getJSONObject(0).get("uuid").toString();
    }

    @SneakyThrows
    private MvcResult plainBankSearch(String keyword, Integer start, Integer max, String xsrfToken) {
        return this.mvc
                .perform(get(FIN_TECH_BANK_SEARCH_URL)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .header("X-XSRF-TOKEN", xsrfToken)
                        .param("keyword", keyword)
                        .param("start", start.toString())
                        .param("max", max.toString()))
                .andDo(print())
                .andReturn();
    }


    @AllArgsConstructor
    @Getter
    private static class LoginBody {
        String username;
        String password;
    }

    @SneakyThrows
    private String readFile(String fileName) {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(fileName), StandardCharsets.UTF_8);
    }

    private String getFilenameBankSearch(String keyword, Integer start, Integer max) {
        return BANK_SEARCH_RESPONSE_PREFIX
                + "-" + keyword
                + "-" + start
                + "-" + max
                + POSTFIX;
    }

    private String getFilenameBankProfile(String bankUUID) {
        return BANK_PROFILE_RESPONSE_PREFIX
                + "-" + bankUUID
                + POSTFIX;
    }

    @EnableFinTechImplConfig
    @SpringBootApplication
    public static class TestConfig {

        @Bean
        public MockFeign mockFeign() {
            return new MockFeign();
        }

        public class MockFeign implements FinTechImplConfig.TppBankSearchClient {

            @Override
            public ResponseEntity<BankProfileResponse> bankProfileGET(String authorization, UUID xRequestID, @NotNull @Valid String bankId) {
                return null;
            }

            @Override
            public ResponseEntity<BankSearchResponse> bankSearchGET(String authorization, UUID xRequestID, @NotNull @Valid String keyword, @Valid Integer start, @Valid Integer max) {
                return null;
            }
        }
    }
}

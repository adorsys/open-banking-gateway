package de.adorsys.opba.fintech.server;

import com.google.gson.Gson;
import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.service.BankSearchService;
import de.adorsys.opba.tpp.bankserach.api.model.BankProfileResponse;
import de.adorsys.opba.tpp.bankserach.api.model.BankSearchResponse;
import de.adorsys.opba.tpp.bankserach.api.resource.TppBankSearchApi;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@EnableFinTechImplConfig
@Slf4j
class FinTechServerTests {
    private static final String FIN_TECH_AUTH_URL = "/v1/login";
    private static final String FIN_TECH_BANK_SEARCH_URL = "/v1/search/bankSearch";
    private static final String FIN_TECH_BANK_PROFILE_URL = "/v1/search/bankProfile";

    private static final String BANK_SEARCH_RESPONSE_PREFIX = "TPP_BankSearchResponse";
    private static final String BANK_PROFILE_RESPONSE_PREFIX = "TPP_BankProfileResponse";
    private static final String POSTFIX = ".json";

    private static final Gson gson = new Gson();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Autowired
    protected MockMvc mvc;

    @Test
    @SneakyThrows
    public void loginPostOk() {
        auth("peter", "1234");
    }

    /**
     *
     * @param username
     * @param password
     * @return XSRF Token
     */
    @SneakyThrows
    private String auth(String username, String password) {
        LoginBody loginBody = new LoginBody(username, password);
        MvcResult result = this.mvc
                .perform(MockMvcRequestBuilders.post(FIN_TECH_AUTH_URL)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .content(gson.toJson(loginBody))
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
        final LoginBody loginBody = new LoginBody("peter", "12345");
        this.mvc
                .perform(MockMvcRequestBuilders.post(FIN_TECH_AUTH_URL)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .content(gson.toJson(loginBody))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.header().doesNotExist("X-XSRF-TOKEN"));
    }

    @Mock
    TppBankSearchApi mockedTppBankSearchApi;

    @Autowired
    BankSearchService bankSearchService;

    @Test
    @SneakyThrows
    public void bankSearchAuthorized() {
        final String keyword = "affe";
        final Integer start = 1;
        final Integer max = 2;

        bankSearchService.setTppBankSearchApi(mockedTppBankSearchApi);

        when(mockedTppBankSearchApi.bankSearchGET(any(), any(), eq(keyword), eq(start), eq(max)))
                .thenReturn(gson.fromJson(readFile(getFilenameBankSearch(keyword, start, max)), BankSearchResponse.class));

        LoginBody loginBody = new LoginBody("peter", "1234");
        String xsrfToken = auth(loginBody.username, loginBody.password);
        bankSearch(keyword, start, max, xsrfToken);
    }

    /**
     *
     * @param keyword
     * @param start
     * @param max
     * @param xsrfToken
     * @return first BankUUID of found list
     */
    @SneakyThrows
    private String bankSearch(String keyword, Integer start, Integer max, String xsrfToken) {
        MvcResult mvcResult = this.mvc
                .perform(MockMvcRequestBuilders.get(FIN_TECH_BANK_SEARCH_URL)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .header("X-XSRF-TOKEN", xsrfToken)
                        .param("keyword", keyword)
                        .param("start", start.toString())
                        .param("max", max.toString()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        return new JSONObject(mvcResult.getResponse().getContentAsString()).getJSONArray("bankDescriptor").getJSONObject(0).get("uuid").toString();
    }

    @Test
    @SneakyThrows
    public void bankSearchUnAuthorized() {
        String xsrfToken = "unvalidtoken";
        this.mvc
                .perform(MockMvcRequestBuilders.get(FIN_TECH_BANK_SEARCH_URL)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .header("X-XSRF-TOKEN", xsrfToken)
                        .param("keyword", "affe")
                        .param("start", "0")
                        .param("max", "2"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Tnis test does
     * authorization
     * bankSearch
     * bankProfile
     */
    @Test
    @SneakyThrows
    public void bankProfileAuthorized() {
        bankSearchService.setTppBankSearchApi(mockedTppBankSearchApi);

        String xsrfToken = null;
        {
            final String user = "peter";
            final String password = "1234";
            log.info("DO Authorization ({}, {}) ==============================", user, password);
            LoginBody loginBody = new LoginBody(user, password);
            xsrfToken = auth(loginBody.username, loginBody.password);
        }

        String bankUUID = null;
        {
            final String keyword = "affe";
            final Integer start = 1;
            final Integer max = 2;
            log.info("DO Bank Search ({}, {}, {}) ==============================", keyword, start, max);

            when(mockedTppBankSearchApi.bankSearchGET(any(), any(), eq(keyword), eq(start), eq(max)))
                    .thenReturn(gson.fromJson(readFile(getFilenameBankSearch(keyword, start, max)), BankSearchResponse.class));

            bankUUID = bankSearch(keyword, start, max, xsrfToken);
        }

        List<String> services = null;
        {
            log.info("DO Bank Profile ({}) ============================== ", bankUUID);
            when(mockedTppBankSearchApi.bankProfileGET(any(), any(), eq(bankUUID)))
                    .thenReturn(gson.fromJson(readFile(getFilenameBankProfile(bankUUID)), BankProfileResponse.class));

            services = bankProfile(xsrfToken, bankUUID);
            Assertions.assertTrue(services.containsAll(Arrays.asList(new String[]{"List accounts", "List transactions", "Initiate payment"})));
        }
        log.info(" i have done it");
    }

    private List<String> bankProfile(String xsrfToken, String bankUUID) throws Exception {
        MvcResult mvcResult = this.mvc
                .perform(MockMvcRequestBuilders.get(FIN_TECH_BANK_PROFILE_URL)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .header("X-XSRF-TOKEN", xsrfToken)
                        .param("bankId", bankUUID))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<String> result = new ArrayList<>();
        JSONArray jsonArray = new JSONObject(mvcResult.getResponse().getContentAsString()).getJSONObject("bankProfile").getJSONArray("services");
        for (int i = 0; i < jsonArray.length(); i++) {
            result.add(jsonArray.getString(i));
        }
        return result;
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

}

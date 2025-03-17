package de.adorsys.opba.fintech.server;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
import de.adorsys.opba.fintech.impl.tppclients.Consts;
import de.adorsys.opba.fintech.server.config.TestConfig;
import de.adorsys.opba.fintech.server.feignmocks.TppBankSearchClientFeignMock;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankProfileResponse;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankSearchResponse;
import de.adorsys.opba.tpp.banksearch.api.resource.generated.TppBankSearchApi;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.servlet.http.Cookie;
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
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@EnableFinTechImplConfig
@Slf4j
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class FinTechBankSearchApiTest extends FinTechApiBaseTest {
    public static final String FIN_TECH_AUTH_URL = "/v1/login";
    private static final String FIN_TECH_AUTH_LOGOUT_URL = "/v1/logout";
    private static final String FIN_TECH_BANK_SEARCH_URL = "/v1/search/bankSearch";
    private static final String FIN_TECH_BANK_PROFILE_URL = "/v1/search/bankProfile";

    @MockBean(reset = MockReset.NONE, answer = Answers.CALLS_REAL_METHODS)
    protected RestRequestContext restRequestContext;

    @MockBean
    protected TppBankSearchClientFeignMock tppBankSearchClientFeignMock;

    @SuppressWarnings("PMD.UnusedPrivateField")
    @MockBean
    protected TppBankSearchApi mockedTppBankSearchApi;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    public void finish() {
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
        plainauth("peter", "1234");
        MvcResult result = plainauth("peter", "12345");
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
        assertNull(result.getResponse().getCookie("XSRF-TOKEN"));
    }

    @Test
    @SneakyThrows
    public void logoutPostOk() {
        authOk("peter", "1234");
        MvcResult result = plainLogout();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    @SneakyThrows
    public void logoutPostNotOk() {
        restRequestContext.setRequestId(UUID.randomUUID().toString());
        restRequestContext.setXsrfTokenHeaderField(UUID.randomUUID().toString());
        MvcResult mvcResult = plainLogout();
        assertEquals(HttpStatus.UNAUTHORIZED.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    @SneakyThrows
    public void bankSearchAuthorized() {
        final String keyword = "affe";
        final Integer start = 1;
        final Integer max = 2;

        when(tppBankSearchClientFeignMock.bankSearchGET(any(), any(), any(), any(), eq(keyword), eq(start), eq(max), any()))
                .thenReturn(ResponseEntity.ok(GSON.fromJson(readFile(getFilenameBankSearch(keyword, start, max)), BankSearchResponse.class)));

        LoginBody loginBody = new LoginBody("peter", "1234");
        authOk(loginBody.username, loginBody.password);
        bankSearchOk(keyword, start, max);
    }

    @Test
    @SneakyThrows
    public void bankSearchUnAuthorized() {
        restRequestContext.setRequestId(UUID.randomUUID().toString());
        restRequestContext.setXsrfTokenHeaderField(UUID.randomUUID().toString());
        MvcResult result = plainBankSearch("affe", 0, 2);
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
        getBankProfileTestResult();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    static class BankProfileTestResult {
        String sessionCookieValue = null;
        UUID bankUUID = null;
        List<String> services = null;

    }

    BankProfileTestResult getBankProfileTestResult() {
        BankProfileTestResult result = new BankProfileTestResult();
        {
            final String user = "peter";
            final String password = "1234";
            log.info("DO Authorization ({}, {}) ==============================", user, password);
            LoginBody loginBody = new LoginBody(user, password);
            result.setSessionCookieValue(authOk(loginBody.username, loginBody.password));
        }

        {
            final String keyword = "affe";
            final Integer start = 1;
            final Integer max = 2;
            log.info("DO Bank Search ({}, {}, {}) ==============================", keyword, start, max);

            when(tppBankSearchClientFeignMock.bankSearchGET(any(), any(), any(), any(), eq(keyword), eq(start), eq(max), any()))
                    .thenReturn(ResponseEntity.ok(GSON.fromJson(readFile(getFilenameBankSearch(keyword, start, max)), BankSearchResponse.class)));

            result.setBankUUID(bankSearchOk(keyword, start, max));
        }

        {
            log.info("DO Bank Profile ({}) ============================== ", result.getBankUUID());
            when(tppBankSearchClientFeignMock.bankProfileGET(any(), eq(result.getBankUUID()), any(), any(), any(), any()))
                    .thenReturn(ResponseEntity.ok(GSON.fromJson(readFile(getFilenameBankProfile(result.getBankUUID())), BankProfileResponse.class)));

            result.setServices(bankProfile(result.getBankUUID()));
            assertTrue(result.getServices().containsAll(Arrays.asList(new String[]{"List accounts", "List transactions", "Initiate payment"})));
        }
        log.info(" i have done it");
        return result;
    }

    /**
     * @param bankProfileUUID
     * @return List of Services of Bank
     */
    @SneakyThrows
    List<String> bankProfile(UUID bankProfileUUID) {
        MvcResult mvcResult = this.mvc
                .perform(get(FIN_TECH_BANK_PROFILE_URL)
                        .header(Consts.HEADER_X_REQUEST_ID, UUID.randomUUID().toString())
                        .header(Consts.HEADER_XSRF_TOKEN, restRequestContext.getXsrfTokenHeaderField())
                        .param("bankProfileId", bankProfileUUID.toString()))
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
    String authOk(String username, String password) {
        MvcResult result = plainauth(username, password);
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(result.getResponse().getHeader(Consts.HEADER_XSRF_TOKEN));

        log.info("session cookie after login: {}", restRequestContext.getSessionCookieValue());
        log.info("xsrftoken after login:      {}", restRequestContext.getXsrfTokenHeaderField());

        return restRequestContext.getSessionCookieValue();
    }

    @SneakyThrows
    MvcResult plainauth(String username, String password) {
        LoginBody loginBody = new LoginBody(username, password);
        restRequestContext.setRequestId(UUID.randomUUID().toString());
        MvcResult mvcResult = this.mvc
                .perform(post(FIN_TECH_AUTH_URL)
                        .header(Consts.HEADER_X_REQUEST_ID, restRequestContext.getRequestId())
                        .content(GSON.toJson(loginBody))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        String sessionValue = null;
        Cookie sessionCookie = mvcResult.getResponse().getCookie(Consts.COOKIE_SESSION_COOKIE_NAME);
        if (sessionCookie != null) {
            sessionValue = mvcResult.getResponse().getCookie(Consts.COOKIE_SESSION_COOKIE_NAME).getValue();
        }
        String xsrfToken = mvcResult.getResponse().getHeader(Consts.HEADER_XSRF_TOKEN);
        restRequestContext.setSessionCookieValue(sessionValue);
        restRequestContext.setXsrfTokenHeaderField(xsrfToken);
        log.info("AFTER TEST LOGIN RestRequestContext is {}", restRequestContext.toString());
        return mvcResult;
    }

    @SneakyThrows
    MvcResult plainLogout() {
        return this.mvc
                .perform(post(FIN_TECH_AUTH_LOGOUT_URL)
                        .header(Consts.HEADER_X_REQUEST_ID, restRequestContext.getRequestId())
                        .header(Consts.HEADER_XSRF_TOKEN, restRequestContext.getXsrfTokenHeaderField())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
    }

    /**
     * @param keyword
     * @param start
     * @param max
     * @return first BankUUID of found list
     */
    @SneakyThrows
    UUID bankSearchOk(String keyword, Integer start, Integer max) {
        MvcResult result = plainBankSearch(keyword, start, max);
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        return UUID.fromString(new JSONObject(result.getResponse().getContentAsString()).getJSONArray("bankDescriptor").getJSONObject(0).get("uuid").toString());
    }

    @SneakyThrows
    MvcResult plainBankSearch(String keyword, Integer start, Integer max) {
        return this.mvc
                .perform(get(FIN_TECH_BANK_SEARCH_URL)
                        .header(Consts.HEADER_X_REQUEST_ID, restRequestContext.getRequestId())
                        .header(Consts.HEADER_XSRF_TOKEN, restRequestContext.getXsrfTokenHeaderField())
                        .param("keyword", keyword)
                        .param("start", start.toString())
                        .param("max", max.toString()))
                .andDo(print())
                .andReturn();
    }
}

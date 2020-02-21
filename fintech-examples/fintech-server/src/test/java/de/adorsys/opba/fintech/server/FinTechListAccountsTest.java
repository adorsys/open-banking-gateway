package de.adorsys.opba.fintech.server;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.server.config.TestConfig;
import de.adorsys.opba.fintech.server.feignmocks.TppAisClientFeignMock;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import de.adorsys.opba.tpp.ais.api.resource.generated.TppBankingApiAccountInformationServiceAisApi;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.AUTHORIZATION_SESSION_ID;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.PSU_CONSENT_SESSION;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.REDIRECT_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@EnableFinTechImplConfig
@Slf4j
public class FinTechListAccountsTest extends FinTechBankSearchApiTest {
    private static final String FIN_TECH_LIST_ACCOUNTS_URL = "/v1/ais/banks/{bank-id}/accounts";

    @MockBean
    protected TppAisClientFeignMock tppAisClientFeignMock;

    @SuppressWarnings("PMD.UnusedPrivateField")
    @MockBean
    protected TppBankingApiAccountInformationServiceAisApi mockedTppBankingApiAccountInformationServiceAisApi;

    @Test
    @SneakyThrows
    public void testListAccountsFor200() {
        BankProfileTestResult result = getBankProfileTestResult();
        List<String> accountIDs = listAccountsForOk(result);
        assertTrue(accountIDs.containsAll(Arrays.asList(new String[]{"12345", "67890"})));
    }

    @SneakyThrows
    List<String> listAccountsForOk(BankProfileTestResult result) {
        when(tppAisClientFeignMock.getAccounts(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(ResponseEntity.ok(GSON.fromJson(readFile("TPP_LIST_ACCOUNTS.json"), AccountList.class)));

        MvcResult mvcResult = plainListAccounts(result.getXsrfToken(), result.getBankUUID());
        assertEquals(OK.value(), mvcResult.getResponse().getStatus());
        log.info("GOT RESULT STRING: {}", mvcResult.getResponse().getContentAsString());
        List<String> accountIDs = new ArrayList<>();
        JSONArray accounts = new JSONObject(mvcResult.getResponse().getContentAsString())
                .getJSONArray("accounts");
        for (int i = 0; i < accounts.length(); i++) {
            accountIDs.add(accounts.getJSONObject(i).getString("resourceId"));
        }
        return accountIDs;
    }

    @Test
    @SneakyThrows
    public void testListAccountsFor303() {
        ResponseEntity<AccountList> accepted = ResponseEntity.accepted()
                .header(AUTHORIZATION_SESSION_ID, "1")
                .header(REDIRECT_CODE, "redirectCode")
                .header(PSU_CONSENT_SESSION, "2")
                .location(new URI("affe"))
                .build();
        BankProfileTestResult result = getBankProfileTestResult();
        when(tppAisClientFeignMock.getAccounts(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(accepted);

        MvcResult mvcResult = plainListAccounts(result.getXsrfToken(), result.getBankUUID());
        assertEquals(FOUND.value(), mvcResult.getResponse().getStatus());
        assertEquals("redirectCode", mvcResult.getResponse().getHeader(REDIRECT_CODE));
    }


    @SneakyThrows
    MvcResult plainListAccounts(String xsrfToken, String bankUUID) {
        log.info("xsrfToken {} bankUUID {}", xsrfToken, bankUUID);
        return this.mvc
                .perform(get(FIN_TECH_LIST_ACCOUNTS_URL, bankUUID)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .header("X-XSRF-TOKEN", xsrfToken)
                        .header("Fintech-Redirect-URL-OK", "ok")
                        .header("Fintech-Redirect-URL-NOK", "notok"))
                .andDo(print())
                .andReturn();
    }

    @Override
    public void bankProfileAuthorized() {
    }

    @Override
    public void bankSearchAuthorized() {
    }

    @Override
    public void bankSearchUnAuthorized() {
    }

    @Override
    public void loginPostOk() {
    }

    @Override
    public void loginPostUnAuthorized() {
    }

}

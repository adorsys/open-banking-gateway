package de.adorsys.opba.fintech.server;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.tppclients.Consts;
import de.adorsys.opba.fintech.server.config.TestConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@EnableFinTechImplConfig
@Slf4j
@ActiveProfiles("withmock")
public class FinTechListAccountsWithMockTest extends FinTechBankSearchApiTest {
    private static final String FIN_TECH_LIST_ACCOUNTS_URL = "/v1/ais/banks/{bank-id}/accounts";

    @Test
    public void testListAccounts() {
        BankProfileTestResult result = getBankProfileTestResult();
        List<String> accountIDs = listAccountIDs(result.getBankUUID());
        accountIDs.forEach(a -> log.info("found: {}", a));
        assertTrue(accountIDs.containsAll(Arrays.asList(new String[]{"firstAccount", "secondAccount"})));
    }

    @SneakyThrows
    List<String> listAccountIDs(String bankUUID) {
        MvcResult mvcResult = this.mvc
                .perform(get(FIN_TECH_LIST_ACCOUNTS_URL, bankUUID)
                        .header(Consts.HEADER_X_REQUEST_ID, restRequestContext.getRequestId())
                        .header(Consts.HEADER_XSRF_TOKEN, restRequestContext.getXsrfTokenHeaderField())
                        .header("Fintech-Redirect-URL-OK", "ok")
                        .header("Fintech-Redirect-URL-NOK", "notok"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<String> accountIDs = new ArrayList<>();
        JSONArray accounts = new JSONObject(mvcResult.getResponse().getContentAsString())
                .getJSONArray("accounts");
        for (int i = 0; i < accounts.length(); i++) {
            accountIDs.add(accounts.getJSONObject(i).getString("resourceId"));
        }
        return accountIDs;
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

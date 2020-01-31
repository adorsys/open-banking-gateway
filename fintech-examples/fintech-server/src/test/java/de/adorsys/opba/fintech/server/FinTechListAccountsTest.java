package de.adorsys.opba.fintech.server;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.server.config.TestConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@EnableFinTechImplConfig
@Slf4j
public class FinTechListAccountsTest extends FinTechBankSearchApiTest {
    private static final String FIN_TECH_LIST_ACCOUNTS_URL = "/v1/ais/banks/{bank-id}/accounts";

    @Test
    public void testListAccounts() {
        BankProfileTestResult result = getBankProfileTestResult();
        List<String> ibans = listAccounts(result.getXsrfToken(), result.getBankUUID());
        assertTrue(ibans.containsAll(Arrays.asList(new String[]{"FR7612345987650123456789014", "AFFE12345987650123456789014"})));
    }

    @SneakyThrows
    List<String> listAccounts(String xsrfToken, String bankUUID) {
        MvcResult mvcResult = this.mvc
                .perform(get(FIN_TECH_LIST_ACCOUNTS_URL, bankUUID)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .header("X-XSRF-TOKEN", xsrfToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<String> ibans = new ArrayList<>();
        JSONArray accounts = new JSONObject(mvcResult.getResponse().getContentAsString())
                .getJSONObject("accountList")
                .getJSONArray("accounts");
        for (int i = 0; i < accounts.length(); i++) {
            ibans.add(accounts.getJSONObject(i).getString("iban"));
        }
        return ibans;
    }


}

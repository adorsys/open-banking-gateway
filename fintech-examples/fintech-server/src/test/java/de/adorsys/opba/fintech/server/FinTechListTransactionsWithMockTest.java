package de.adorsys.opba.fintech.server;

import de.adorsys.opba.fintech.impl.tppclients.Consts;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.SERVICE_SESSION_ID;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class FinTechListTransactionsWithMockTest extends FinTechListAccountsWithMockTest {
    private static final String FIN_TECH_LIST_TRANSACTIONS_URL = "/v1/ais/banks/{bank-id}/accounts/{account-id}/transactions";

    @Test
    public void testListTransactions() {
        BankProfileTestResult result = getBankProfileTestResult();
        List<String> accountIDs = listAccountIDs(result.getBankUUID());
        List<String> amounts = listAmounts(result.getBankUUID(), accountIDs.get(0));
        assertTrue(amounts.containsAll(Arrays.asList(new String[]{"123"})));
    }

    @SneakyThrows
    List<String> listAmounts(String bankUUID, String accountID) {
        MvcResult mvcResult = this.mvc
                .perform(get(FIN_TECH_LIST_TRANSACTIONS_URL, bankUUID, accountID)
                        .header(Consts.HEADER_X_REQUEST_ID, restRequestContext.getRequestId())
                        .header(Consts.HEADER_XSRF_TOKEN, restRequestContext.getXsrfTokenHeaderField())
                        .header("Fintech-Redirect-URL-OK", "ok")
                        .header("Fintech-Redirect-URL-NOK", "notok")
                        .header(SERVICE_SESSION_ID, "any-session-not-specified-in api.yml yet")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<String> amountList = new ArrayList<>();
        JSONArray booked = new JSONObject(mvcResult.getResponse().getContentAsString())
                .getJSONObject("transactions")
                .getJSONArray("booked");
        for (int i = 0; i < booked.length(); i++) {
            amountList.add(booked.getJSONObject(i).getJSONObject("transactionAmount").getString("amount"));
        }
        return amountList;
    }

    @Override
    public void testListAccounts() {
    }

}

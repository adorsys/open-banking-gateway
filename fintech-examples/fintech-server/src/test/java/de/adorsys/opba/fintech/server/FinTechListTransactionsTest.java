package de.adorsys.opba.fintech.server;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class FinTechListTransactionsTest extends FinTechListAccountsTest {
    private static final String FIN_TECH_LIST_TRANSACTIONS_URL = "/v1/ais/banks/{bank-id}/accounts/{account-id}/transactions";

    @Test
    public void testListTransactions() {
        BankProfileTestResult result = getBankProfileTestResult();
        List<String> accountIDs = listAccountIDs(result.getXsrfToken(), result.getBankUUID());
        List<String> amounts = listAmounts(result.getXsrfToken(), result.getBankUUID(), accountIDs.get(0));
        assertTrue(amounts.containsAll(Arrays.asList(new String[]{"123"})));
    }

    @SneakyThrows
    List<String> listAmounts(String xsrfToken, String bankUUID, String accountID) {
        MvcResult mvcResult = this.mvc
                .perform(get(FIN_TECH_LIST_TRANSACTIONS_URL, bankUUID, accountID)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .header("X-XSRF-TOKEN", xsrfToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<String> ammountList = new ArrayList<>();
        log.info("=============");
        log.info(mvcResult.getResponse().getContentAsString());
        log.info("=============");

        JSONArray booked = new JSONObject(mvcResult.getResponse().getContentAsString())
                .getJSONObject("accountList")
                .getJSONObject("transactions")
                .getJSONArray("booked");
        for (int i = 0; i < booked.length(); i++) {
            ammountList.add(booked.getJSONObject(i).getJSONObject("transactionAmount").getString("amount"));
        }
        return ammountList;


    }

}

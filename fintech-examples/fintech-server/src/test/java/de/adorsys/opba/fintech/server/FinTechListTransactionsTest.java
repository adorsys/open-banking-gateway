package de.adorsys.opba.fintech.server;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FinTechListTransactionsTest extends FinTechListAccountsTest {
    private static final String FIN_TECH_LIST_TRANSACTIONS_URL = "/v1/ais/banks/{bank-id}/accounts/{account-id}/transactions";

    @Test
    public void testListTransactions() {
        BankProfileTestResult result = getBankProfileTestResult();
        List<String> accountIDs = listAccountIDs(result.getXsrfToken(), result.getBankUUID());
        listTransactions(result.getXsrfToken(), result.getBankUUID(), accountIDs.get(0));
        // assertTrue(accountIDs.containsAll(Arrays.asList(new String[]{"firstAccount", "secondAccount"})));
    }

    @SneakyThrows
    void listTransactions(String xsrfToken, String bankUUID, String accountID) {
        MvcResult mvcResult = this.mvc
                .perform(get(FIN_TECH_LIST_TRANSACTIONS_URL, bankUUID, accountID)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .header("X-XSRF-TOKEN", xsrfToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

    }

}

package de.adorsys.opba.fintech.server;

import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class FinTechListTransactionsTest extends FinTechListAccountsTest {
    private static final String FIN_TECH_LIST_TRANSACTIONS_URL = "/v1/ais/banks/{bank-id}/accounts/{account-id}/transactions";

    @Test
    @SneakyThrows
    public void testListTransactionsForOk() {
        BankProfileTestResult result = getBankProfileTestResult();
        List<String> accountIDs = listAccountsForOk(result);
        when(tppAisClientFeignMock.getTransactions(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(ResponseEntity.ok(GSON.fromJson(readFile("TPP_LIST_TRANSACTIONS.json"), TransactionsResponse.class)));
        List<String> amounts = listAmounts(result.getXsrfToken(), result.getBankUUID(), accountIDs.get(0));
        assertTrue(amounts.containsAll(Arrays.asList(new String[]{"1000"})));
    }

    @Test
    @SneakyThrows
    public void testListTransactionsForRedirect() {
        ResponseEntity<TransactionsResponse> accepted = ResponseEntity.accepted()
                .header(AUTHORIZATION_SESSION_ID, "1")
                .header(REDIRECT_CODE, "redirectCode")
                .header(PSU_CONSENT_SESSION, "2")
                .location(new URI("affe"))
                .build();

        BankProfileTestResult result = getBankProfileTestResult();
        List<String> accountIDs = listAccountsForOk(result);
        when(tppAisClientFeignMock.getTransactions(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(accepted);
        MvcResult mvcResult = plainListAmounts(result.getXsrfToken(), result.getBankUUID(), accountIDs.get(0));
        assertEquals(HttpStatus.FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @SneakyThrows
    List<String> listAmounts(String xsrfToken, String bankUUID, String accountID) {
        MvcResult mvcResult = plainListAmounts(xsrfToken, bankUUID, accountID);
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        List<String> amountList = new ArrayList<>();
        JSONArray booked = new JSONObject(mvcResult.getResponse().getContentAsString())
                .getJSONObject("transactions")
                .getJSONArray("booked");
        for (int i = 0; i < booked.length(); i++) {
            amountList.add(booked.getJSONObject(i).getJSONObject("transactionAmount").getString("amount"));
        }
        return amountList;
    }

    private MvcResult plainListAmounts(String xsrfToken, String bankUUID, String accountID) throws Exception {
        return this.mvc
                .perform(get(FIN_TECH_LIST_TRANSACTIONS_URL, bankUUID, accountID)
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .header("X-XSRF-TOKEN", xsrfToken)
                        .header("Fintech-Redirect-URL-OK", "ok")
                        .header("Fintech-Redirect-URL-NOK", "notok"))
                .andDo(print())
                .andReturn();
    }

    @Override
    public void testListAccountsFor200() {
    }

    @Override
    public void testListAccountsFor303() {
    }
}

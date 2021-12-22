package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.api.security.requestsigner.OpenBankingDataToSignProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.FINTECH_ID;
import static de.adorsys.opba.api.security.external.domain.HttpHeaders.X_REQUEST_ID;
import static de.adorsys.opba.api.security.external.domain.HttpHeaders.X_TIMESTAMP_UTC;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TestTppBankSearchController extends BaseTppBankSearchControllerTest {
    private static final String EMPTY_STRING = "";

    @Test
    void testBankSearch() throws Exception {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();
        String keyword = "commerz";

        performBankSearchRequest(xRequestId, xTimestampUtc, keyword)
                .andExpect(jsonPath("$.bankDescriptor.length()").value("10"))
                .andExpect(jsonPath("$.bankDescriptor[0].bankName").value("Commerzbank"))
                .andExpect(jsonPath("$.bankDescriptor[0].bic").value("COBADEFFXXX"))
                .andExpect(jsonPath("$.bankDescriptor[0].bankCode").value("35640064"))
                .andExpect(jsonPath("$.bankDescriptor[0].uuid").value("291b2ca1-b35f-463e-ad94-2a1a26c09304"))
                .andReturn();
    }

    @Test
    void testBankSearchWithNullKeyword() throws Exception {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        performBankSearchRequest(xRequestId, xTimestampUtc, null)
                .andExpect(jsonPath("$.bankDescriptor.length()").value("0"))
                .andExpect(jsonPath("$.keyword").value(EMPTY_STRING))
                .andReturn();
    }

    @Test
    void testBankSearchWithEmptyKeyword() throws Exception {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        performBankSearchRequest(xRequestId, xTimestampUtc, EMPTY_STRING)
                .andExpect(jsonPath("$.bankDescriptor.length()").value("0"))
                .andExpect(jsonPath("$.keyword").value(EMPTY_STRING))
                .andReturn();
    }

    @Test
    void testBankSearchWithBlankKeyword() throws Exception {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();
        String keyword = " ";

        performBankSearchRequest(xRequestId, xTimestampUtc, keyword)
                .andExpect(jsonPath("$.bankDescriptor.length()").value("0"))
                .andExpect(jsonPath("$.keyword").value(" "))
                .andReturn();
    }

    @Test
    void testBankProfile() throws Exception {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        mockMvc.perform(
                get("/v1/banking/search/bank-profile")
                        .header(X_REQUEST_ID, xRequestId)
                        .header(X_TIMESTAMP_UTC, xTimestampUtc)
                        .header(FINTECH_ID, "MY-SUPER-FINTECH-ID")
                        .param("bankProfileId", "0e8ea18c-4c9c-4c1e-aa20-1cba7abbbd6f")
                        .with(new SignaturePostProcessor(requestSigningService, new OpenBankingDataToSignProvider())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankProfileDescriptor.bankName").value("VR Bank Fulda eG"))
                .andExpect(jsonPath("$.bankProfileDescriptor.bankUuid").value("fcfe98fe-5514-4992-8f36-8239f3a74571"))
                .andExpect(jsonPath("$.bankProfileDescriptor.bic").value("GENODE51FUL"))
                .andReturn();
    }

    @Test
    void testSimilarityThreshold() throws Exception {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();
        String keyword = "Sandbox";

        performBankSearchRequest(xRequestId, xTimestampUtc, keyword)
                .andExpect(jsonPath("$.bankDescriptor.length()").value("0"))
                .andReturn();
    }
}

package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.api.security.requestsigner.OpenBankingDataToSignProvider;
import de.adorsys.opba.tppbankingapi.BaseMockitoTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.UUID;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.FINTECH_ID;
import static de.adorsys.opba.api.security.external.domain.HttpHeaders.X_REQUEST_ID;
import static de.adorsys.opba.api.security.external.domain.HttpHeaders.X_TIMESTAMP_UTC;
import static de.adorsys.opba.tppbankingapi.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles({ONE_TIME_POSTGRES_RAMFS, "test-search"})
@AutoConfigureMockMvc
class TestTppBankSearchController extends BaseMockitoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestSigningService requestSigningService;

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

    @NotNull
    private ResultActions performBankSearchRequest(UUID xRequestId, Instant xTimestampUtc, String keyword) throws Exception {

        return mockMvc.perform(
                get("/v1/banking/search/bank-search")
                        .header(X_REQUEST_ID, xRequestId)
                        .header(X_TIMESTAMP_UTC, xTimestampUtc)
                        .header(FINTECH_ID, "MY-SUPER-FINTECH-ID")
                        .param("keyword", keyword)
                        .param("max", "10")
                        .param("start", "0")
                        .with(new SignaturePostProcessor(requestSigningService, new OpenBankingDataToSignProvider()))
        ).andExpect(status().isOk());
    }
}

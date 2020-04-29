package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.api.security.external.domain.DataToSign;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.tppbankingapi.BaseMockitoTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.FINTECH_ID;
import static de.adorsys.opba.api.security.external.domain.HttpHeaders.X_REQUEST_ID;
import static de.adorsys.opba.api.security.external.domain.HttpHeaders.X_REQUEST_SIGNATURE;
import static de.adorsys.opba.api.security.external.domain.HttpHeaders.X_TIMESTAMP_UTC;
import static de.adorsys.opba.tppbankingapi.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles(ONE_TIME_POSTGRES_RAMFS)
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

        mockMvc.perform(
                get("/v1/banking/search/bank-search")
                        .header("Compute-PSU-IP-Address", "true")

                        .header(X_REQUEST_ID, xRequestId)
                        .header(X_TIMESTAMP_UTC, xTimestampUtc)
                        .header(X_REQUEST_SIGNATURE, requestSigningService.signature(new DataToSign(xRequestId, xTimestampUtc)))
                        .header(FINTECH_ID, "MY-SUPER-FINTECH-ID")
                        .param("keyword", "commerz")
                        .param("max", "10")
                        .param("start", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankDescriptor.length()").value("10"))
                .andExpect(jsonPath("$.bankDescriptor[0].bankName").value("Commerzbank"))
                .andExpect(jsonPath("$.bankDescriptor[0].bic").value("COBADEFFXXX"))
                .andExpect(jsonPath("$.bankDescriptor[0].bankCode").value("35640064"))
                .andExpect(jsonPath("$.bankDescriptor[0].uuid").value("291b2ca1-b35f-463e-ad94-2a1a26c09304"))
                .andReturn();
    }

    @Test
    void testBankProfile() throws Exception {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        mockMvc.perform(
                get("/v1/banking/search/bank-profile")
                        .header("Compute-PSU-IP-Address", "true")

                        .header(X_REQUEST_ID, xRequestId)
                        .header(X_TIMESTAMP_UTC, xTimestampUtc)
                        .header(X_REQUEST_SIGNATURE, requestSigningService.signature(new DataToSign(xRequestId, xTimestampUtc)))
                        .header(FINTECH_ID, "MY-SUPER-FINTECH-ID")

                        .param("bankId", "4eee696c-b2d2-45ac-86c7-b77a810a261b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankProfileDescriptor.bankName").value("DZ BANK D3000"))
                .andExpect(jsonPath("$.bankProfileDescriptor.bankUuid").value("4eee696c-b2d2-45ac-86c7-b77a810a261b"))
                .andExpect(jsonPath("$.bankProfileDescriptor.bic").value("GENODE51FUL"))
                .andReturn();
    }

    @Test
    void testSimilarityThreshold() throws Exception {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        mockMvc.perform(
                get("/v1/banking/search/bank-search")
                        .header("Compute-PSU-IP-Address", "true")

                        .header(X_REQUEST_ID, xRequestId)
                        .header(X_TIMESTAMP_UTC, xTimestampUtc)
                        .header(X_REQUEST_SIGNATURE, requestSigningService.signature(new DataToSign(xRequestId, xTimestampUtc)))
                        .header(FINTECH_ID, "MY-SUPER-FINTECH-ID")

                        .param("keyword", "Sandbox")
                        .param("max", "10")
                        .param("start", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankDescriptor.length()").value("0"))
                .andReturn();
    }
}

package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.tppbankingapi.BaseMockitoTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    void testBankSearch() throws Exception {
        mockMvc.perform(
                get("/v1/banking/search/bank-search")
                        .header("Authorization", "123")
                        .header("X-Request-ID", "01f4ec8e-8fb8-4e37-8912-bae6ff227231")
                        .param("keyword", "commerz")
                        .param("max", "10")
                        .param("start", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankDescriptor.length()").value("10"))
                .andExpect(jsonPath("$.bankDescriptor[0].bankName").value("Commerzbank"))
                .andExpect(jsonPath("$.bankDescriptor[0].bic").value("COBADEFFXXX"))
                .andExpect(jsonPath("$.bankDescriptor[0].bankCode").value("36040039"))
                .andExpect(jsonPath("$.bankDescriptor[0].uuid").value("dd624199-d071-4c95-b554-179b0e92c707"))
                .andReturn();
    }

    @Test
    void testBankProfile() throws Exception {
        mockMvc.perform(
                get("/v1/banking/search/bank-profile")
                        .header("Authorization", "123")
                        .header("X-Request-ID", "01f4ec8e-8fb8-4e37-8912-bae6ff227231")
                        .param("bankId", "4eee696c-b2d2-45ac-86c7-b77a810a261b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankProfileDescriptor.bankName").value("DZ BANK D3000"))
                .andExpect(jsonPath("$.bankProfileDescriptor.bankUuid").value("4eee696c-b2d2-45ac-86c7-b77a810a261b"))
                .andExpect(jsonPath("$.bankProfileDescriptor.bic").value("GENODE51FUL"))
                .andReturn();
    }
}

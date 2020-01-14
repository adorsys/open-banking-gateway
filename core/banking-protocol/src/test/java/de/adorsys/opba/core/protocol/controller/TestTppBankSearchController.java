package de.adorsys.opba.core.protocol.controller;

import de.adorsys.opba.core.protocol.BaseMockitoTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static de.adorsys.opba.core.protocol.TestProfiles.ONE_TIME_POSTGRES_ON_DISK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@ActiveProfiles(ONE_TIME_POSTGRES_ON_DISK)
class TestTppBankSearchController extends BaseMockitoTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void onSetUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testBankSearch() throws Exception {
        mockMvc.perform(
                get("/v1/banking/search/bankSearch")
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
                get("/v1/banking/search/bankProfile")
                        .header("Authorization", "123")
                        .header("X-Request-ID", "01f4ec8e-8fb8-4e37-8912-bae6ff227231")
                        .param("bankId", "4eee696c-b2d2-45ac-86c7-b77a810a261b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankProfileDescriptor.bankName").value("DZ BANK D3000"))
                .andExpect(jsonPath("$.bankProfileDescriptor.bic").value("GENODE51FUL"))
                .andReturn();
    }
}

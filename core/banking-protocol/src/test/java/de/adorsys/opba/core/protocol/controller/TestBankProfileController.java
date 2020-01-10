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
class TestBankProfileController extends BaseMockitoTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void onSetUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void TestBankProfile() throws Exception {
        mockMvc.perform(
                get("/v1/banks/profile")
                        .param("id", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("12"))
                .andExpect(jsonPath("$.url").value("https://www.rbopr.de/services_xs2a"))
                .andExpect(jsonPath("$.adapterId").value("fiducia-adapter"))
                .andExpect(jsonPath("$.idpUrl").value(""))
                .andExpect(jsonPath("$.bank.id").value("12"))
                .andExpect(jsonPath("$.bank.name").value("DZ BANK D3000"))
                .andExpect(jsonPath("$.bank.bic").value("GENODE51FUL"))
                .andExpect(jsonPath("$.bank.bankCode").value("53060180"))
                .andReturn();
    }
}

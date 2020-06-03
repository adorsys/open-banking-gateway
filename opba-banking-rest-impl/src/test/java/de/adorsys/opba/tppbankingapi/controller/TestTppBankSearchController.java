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
                        .header("X-Request-ID", "3ab706f2-8cc8-462e-8393-a43f6ee87e53")
                        .header("Compute-PSU-IP-Address", "true")
                        .header("X-Timestamp-UTC", "2020-04-17T13:45:17.069Z")
                        .header("X-Request-Signature", "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJmaW50ZWNoQGF3ZXNvbWUtZmludGVjaC5jb20iLCJpc3MiOiJmaW50ZWNoLmNvbSIsInNpZ24tZGF0YSI6IjNhYjcwNmYyLThjYzgtNDYyZS04MzkzLWE0M2Y2ZWU4N2U1MzIwMjAtMDQtMTdUMTM6NDU6MTcuMDY5WiJ9.S3L4XdAhlzJBXYHTXMXVNlLmABBkUvYqF03znEmzKQU9vOF-n0cT6yWWjvm6T82ISzZ5OYrJaA2QJekFsw78vraY-t7vxhWVn9hO_C1tJR_rV3SFWi6mtZeuSCGDSJxEB_8gmMqFomQs0sEdBayiC1mkW9R3TQGhmLkXyM4GHGR_rHL1oLFjG3Ueo0tYmLVIJDyQ6oqFHhDdNro41O2E1S9BOOVLbANLU7r_jN8KIuujmFIBF3S7L0P2yvIHQ3Sme3W2550m-LdPI3f2SFD4ZRLG6Xsc8LyrDuXtEuk9H3nHqPenbhQnMPHK7OUcsEN2VFqvUQ9SWTgUz4P9nuU2ng")
                        .header("Fintech-ID", "MY-SUPER-FINTECH-ID")
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
        mockMvc.perform(
                get("/v1/banking/search/bank-profile")
                        .header("X-Request-ID", "3ab706f2-8cc8-462e-8393-a43f6ee87e53")
                        .header("Compute-PSU-IP-Address", "true")
                        .header("X-Timestamp-UTC", "2020-04-17T13:45:17.069Z")
                        .header("X-Request-Signature", "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJmaW50ZWNoQGF3ZXNvbWUtZmludGVjaC5jb20iLCJpc3MiOiJmaW50ZWNoLmNvbSIsInNpZ24tZGF0YSI6IjNhYjcwNmYyLThjYzgtNDYyZS04MzkzLWE0M2Y2ZWU4N2U1MzIwMjAtMDQtMTdUMTM6NDU6MTcuMDY5WiJ9.S3L4XdAhlzJBXYHTXMXVNlLmABBkUvYqF03znEmzKQU9vOF-n0cT6yWWjvm6T82ISzZ5OYrJaA2QJekFsw78vraY-t7vxhWVn9hO_C1tJR_rV3SFWi6mtZeuSCGDSJxEB_8gmMqFomQs0sEdBayiC1mkW9R3TQGhmLkXyM4GHGR_rHL1oLFjG3Ueo0tYmLVIJDyQ6oqFHhDdNro41O2E1S9BOOVLbANLU7r_jN8KIuujmFIBF3S7L0P2yvIHQ3Sme3W2550m-LdPI3f2SFD4ZRLG6Xsc8LyrDuXtEuk9H3nHqPenbhQnMPHK7OUcsEN2VFqvUQ9SWTgUz4P9nuU2ng")
                        .header("Fintech-ID", "MY-SUPER-FINTECH-ID")
                        .param("bankId", "4eee696c-b2d2-45ac-86c7-b77a810a261b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankProfileDescriptor.bankName").value("DZ BANK D3000"))
                .andExpect(jsonPath("$.bankProfileDescriptor.bankUuid").value("4eee696c-b2d2-45ac-86c7-b77a810a261b"))
                .andExpect(jsonPath("$.bankProfileDescriptor.bic").value("GENODE51FUL"))
                .andReturn();
    }

    @Test
    void testSimilarityThreshold() throws Exception {
        mockMvc.perform(
                get("/v1/banking/search/bank-search")
                        .header("X-Request-ID", "3ab706f2-8cc8-462e-8393-a43f6ee87e53")
                        .header("Compute-PSU-IP-Address", "true")
                        .header("X-Timestamp-UTC", "2020-04-17T13:45:17.069Z")
                        .header("X-Request-Signature", "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJmaW50ZWNoQGF3ZXNvbWUtZmludGVjaC5jb20iLCJpc3MiOiJmaW50ZWNoLmNvbSIsInNpZ24tZGF0YSI6IjNhYjcwNmYyLThjYzgtNDYyZS04MzkzLWE0M2Y2ZWU4N2U1MzIwMjAtMDQtMTdUMTM6NDU6MTcuMDY5WiJ9.S3L4XdAhlzJBXYHTXMXVNlLmABBkUvYqF03znEmzKQU9vOF-n0cT6yWWjvm6T82ISzZ5OYrJaA2QJekFsw78vraY-t7vxhWVn9hO_C1tJR_rV3SFWi6mtZeuSCGDSJxEB_8gmMqFomQs0sEdBayiC1mkW9R3TQGhmLkXyM4GHGR_rHL1oLFjG3Ueo0tYmLVIJDyQ6oqFHhDdNro41O2E1S9BOOVLbANLU7r_jN8KIuujmFIBF3S7L0P2yvIHQ3Sme3W2550m-LdPI3f2SFD4ZRLG6Xsc8LyrDuXtEuk9H3nHqPenbhQnMPHK7OUcsEN2VFqvUQ9SWTgUz4P9nuU2ng")
                        .header("Fintech-ID", "MY-SUPER-FINTECH-ID")
                        .param("keyword", "Sandbox")
                        .param("max", "10")
                        .param("start", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankDescriptor.length()").value("0"))
                .andReturn();
    }
}

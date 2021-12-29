package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.api.security.requestsigner.OpenBankingDataToSignProvider;
import de.adorsys.opba.tppbankingapi.BaseMockitoTest;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles({ONE_TIME_POSTGRES_RAMFS, "test-search"})
@AutoConfigureMockMvc
public abstract class BaseTppBankSearchControllerTest extends BaseMockitoTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected RequestSigningService requestSigningService;

    @NotNull
    @SneakyThrows
    protected ResultActions performBankSearchRequest(UUID xRequestId, Instant xTimestampUtc, String keyword) {
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

package de.adorsys.opba.core.protocol.e2e.stages;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.SneakyThrows;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@JGivenStage
public class AccountListResult extends Stage<AccountListResult>  {

    @Autowired
    private MockMvc mvc;

    @ProvidedScenarioState
    private String redirectOkUri;

    @ProvidedScenarioState
    private String responseContent;

    @SneakyThrows
    public AccountListResult open_banking_reads_anton_brueckner_accounts_on_redirect() {
        mvc.perform(asyncDispatch(mvc.perform(get(URI.create(redirectOkUri).getPath())).andReturn()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].iban").value("DE80760700240271232400"))
                .andExpect(jsonPath("$.[*].currency").value("EUR"));
        return self();
    }

    @SneakyThrows
    public AccountListResult open_banking_reads_anton_brueckner_transactions_on_redirect() {
        mvc.perform(asyncDispatch(mvc.perform(get(URI.create(redirectOkUri).getPath())).andReturn()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions.booked").isArray())
                .andExpect(jsonPath("$.transactions.booked[*].transactionId")
                        .value(IsIterableContainingInAnyOrder.containsInAnyOrder(
                                "rnvGvu2TR2Yl99bAoM_skY",
                                "1Lag4mgPRy4kLuz1rRifJ4",
                                "xKVwpTr9TaAoW9j1Zem4Tw",
                                "GrrnMdDgTGIjM-w_kkTVSA",
                                "mfSdvTvYThwr8hocMJMsxA",
                                "Tt7Os27bTc0vC6jDk0f5lY",
                                "qlI0mwopQIknL0n-U4bD80",
                                "pG7GZlccRPsoBNudHnX25Q"
                        ))
                );
        return self();
    }

    @SneakyThrows
    public AccountListResult open_banking_has_max_musterman_accounts() {
        DocumentContext body = JsonPath.parse(responseContent);

        assertThat(body).extracting(it -> it.read("$.[*].iban")).asList().containsExactly("DE38760700240320465700");
        assertThat(body).extracting(it -> it.read("$.[*].currency")).asList().containsExactly("EUR");

        return self();
    }

    @SneakyThrows
    public AccountListResult open_banking_has_max_musterman_transactions() {
        DocumentContext body = JsonPath.parse(responseContent);

        assertThat(body).extracting(it -> it.read("$.transactions.booked[*].transactionId")).asList()
                .containsExactlyInAnyOrder(
                        "VHF5-8R1RCcskezln6CJAY",
                        "etA9KGhIT9ohX9dYXrhzc8",
                        "LjwVWzBBQtwpyQ6WBBTiwk",
                        "pkOyTAHDTb0uCF2R55HKKo",
                        "F3qVhSXlQswswIN2nk1rBo"
                );

        return self();
    }
}

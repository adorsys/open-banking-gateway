package de.adorsys.opba.core.protocol.e2e.stages;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

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

    @SneakyThrows
    public AccountListResult obg_reads_result_on_redirect() {
        mvc.perform(asyncDispatch(mvc.perform(get(URI.create(redirectOkUri).getPath())).andReturn()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].iban").value("DE80760700240271232400"))
                .andExpect(jsonPath("$.[*].currency").value("EUR"))
                .andDo(print());
        return self();
    }
}

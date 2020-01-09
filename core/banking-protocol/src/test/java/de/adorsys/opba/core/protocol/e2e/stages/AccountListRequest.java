package de.adorsys.opba.core.protocol.e2e.stages;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.atomic.AtomicReference;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static de.adorsys.opba.core.protocol.e2e.ResourceUtil.readResource;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_REDIRECT_URI;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@JGivenStage
public class AccountListRequest extends Stage<AccountListRequest> {

    @ProvidedScenarioState
    private String redirectUriToGetUserParams;

    @ProvidedScenarioState
    private String redirectOkUri;

    @Autowired
    private MockMvc mvc;

    @ExpectedScenarioState
    private WireMockServer wireMock;

    @ExpectedScenarioState
    private AtomicReference<String> execId;

    @SneakyThrows
    public AccountListRequest open_banking_list_accounts_called() {
        mvc.perform(asyncDispatch(mvc.perform(get("/v1/accounts")).andReturn()))
                .andExpect(status().is3xxRedirection())
                .andDo(mvcResult -> redirectUriToGetUserParams = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION))
                .andDo(print());
        return self();
    }

    @SneakyThrows
    public AccountListRequest open_banking_user_provided_necessary_details() {
        String executionId = Iterables.getLast(Splitter.on("/").split(Splitter.on("?").split(redirectUriToGetUserParams).iterator().next()));
        execId.set(executionId);
        mvc.perform(
                asyncDispatch(
                        mvc.perform(post("/v1/parameters/provide-more/" + executionId)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .content(readResource("mockedsandbox/restrecord/tpp-ui-input/account.txt")))
                                .andReturn())
        )
                .andExpect(status().is3xxRedirection())
                .andDo(print());

        LoggedRequest consentInitiateRequest = wireMock
                .findAll(postRequestedFor(urlMatching("/v1/consents.*"))).get(0);
        redirectOkUri = consentInitiateRequest.getHeader(TPP_REDIRECT_URI);

        return self();
    }
}

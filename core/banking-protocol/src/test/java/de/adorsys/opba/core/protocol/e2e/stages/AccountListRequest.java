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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

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

    public static final String PARAMETERS_PROVIDE_MORE = "/v1/parameters/provide-more/";

    @ProvidedScenarioState
    private String redirectUriToGetUserParams;

    @ProvidedScenarioState
    private String execId;

    @ProvidedScenarioState
    private String redirectOkUri;

    @ProvidedScenarioState
    private String responseContent;

    @Autowired
    private MockMvc mvc;

    @ExpectedScenarioState
    private WireMockServer wireMock;

    @SneakyThrows
    public AccountListRequest open_banking_list_accounts_called() {
        mvc.perform(asyncDispatch(mvc.perform(get("/v1/accounts")).andReturn()))
                .andExpect(status().is3xxRedirection())
                .andDo(mvcResult -> redirectUriToGetUserParams = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION))
                .andDo(print());
        updateExecutionId();
        return self();
    }

    @SneakyThrows
    public AccountListRequest open_banking_user_anton_brueckner_provided_initial_parameters() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
                "mockedsandbox/restrecord/tpp-ui-input/params/anton-brueckner-account.txt"
        );

        LoggedRequest consentInitiateRequest = wireMock
                .findAll(postRequestedFor(urlMatching("/v1/consents.*"))).get(0);
        redirectOkUri = consentInitiateRequest.getHeader(TPP_REDIRECT_URI);
        return self();
    }

    @SneakyThrows
    public AccountListRequest open_banking_user_max_musterman_provided_initial_parameters() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
                "mockedsandbox/restrecord/tpp-ui-input/params/max-musterman-account.txt"
        );
        return self();
    }

    @SneakyThrows
    public AccountListRequest open_banking_user_max_musterman_provided_password() {
        provideParametersToBankingProtocol(
                "/v1/parameters/provide-psu-password/",
                "mockedsandbox/restrecord/tpp-ui-input/params/max-musterman-password.txt"
        );
        return self();
    }

    @SneakyThrows
    public AccountListRequest open_banking_user_max_musterman_selected_sca_challenge_type() {
        provideParametersToBankingProtocol(
                "/v1/parameters/select-sca-method/",
                "mockedsandbox/restrecord/tpp-ui-input/params/max-musterman-selected-sca.txt"
        );
        return self();
    }

    @SneakyThrows
    public AccountListRequest open_banking_user_max_musterman_provided_sca_challenge_result_and_no_redirect() {
        provideParametersToBankingProtocol(
                "/v1/parameters/report-sca-result/",
                "mockedsandbox/restrecord/tpp-ui-input/params/max-musterman-sca-challenge-result.txt",
                status().isOk()
        );

        return self();
    }

    @SneakyThrows
    private void provideParametersToBankingProtocol(String uriPath, String resource) {
        provideParametersToBankingProtocol(uriPath, resource, status().is3xxRedirection());
        updateExecutionId();
    }

    @SneakyThrows
    private void provideParametersToBankingProtocol(String uriPath, String resource, ResultMatcher matcher) {
        MvcResult result = mvc.perform(
                asyncDispatch(
                        mvc.perform(post(uriPath + execId)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .content(readResource(resource)))
                                .andReturn())
        )
                .andExpect(matcher)
                .andDo(mvcResult -> redirectUriToGetUserParams = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION))
                .andDo(print())
                .andReturn();
        responseContent = result.getResponse().getContentAsString();
    }

    private void updateExecutionId() {
        execId = Iterables.getLast(
                Splitter.on("/").split(Splitter.on("?").split(redirectUriToGetUserParams).iterator().next())
        );
    }
}

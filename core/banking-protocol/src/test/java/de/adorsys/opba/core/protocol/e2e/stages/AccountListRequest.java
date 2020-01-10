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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@JGivenStage
public class AccountListRequest extends Stage<AccountListRequest> {

    public static final String PARAMETERS_PROVIDE_MORE = "/v1/parameters/provide-more/";

    @ProvidedScenarioState
    private String redirectUriToGetUserParams;

    @ProvidedScenarioState
    private String execId;

    @ProvidedScenarioState
    @SuppressWarnings("PMD.UnusedPrivateField") // used by AccountListResult!
    private String redirectOkUri;

    @ProvidedScenarioState
    @SuppressWarnings("PMD.UnusedPrivateField") // used by AccountListResult!
    private String responseContent;

    @Autowired
    private MockMvc mvc;

    @ExpectedScenarioState
    private WireMockServer wireMock;

    @SneakyThrows
    public AccountListRequest open_banking_list_accounts_called() {
        mvc.perform(asyncDispatch(mvc.perform(get("/v1/accounts")).andReturn()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andDo(mvcResult -> redirectUriToGetUserParams = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION));
        updateExecutionId();
        return self();
    }

    @SneakyThrows
    public AccountListRequest open_banking_list_transactions_called_for_anton_brueckner() {
        mvc.perform(asyncDispatch(mvc.perform(get("/v1/transactions/cmD4EYZeTkkhxRuIV1diKA")).andReturn()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andDo(mvcResult -> redirectUriToGetUserParams = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION));
        updateExecutionId();
        return self();
    }

    @SneakyThrows
    public AccountListRequest open_banking_list_transactions_called_for_max_musterman() {
        mvc.perform(asyncDispatch(mvc.perform(get("/v1/transactions/oN7KTVuJSVotMvPPPavhVo")).andReturn()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andDo(mvcResult -> redirectUriToGetUserParams = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION));
        updateExecutionId();
        return self();
    }

    @SneakyThrows
    public AccountListRequest open_banking_user_anton_brueckner_provided_initial_parameters_to_list_accounts() {
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
    public AccountListRequest open_banking_user_anton_brueckner_provided_initial_parameters_to_list_transactions() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
                "mockedsandbox/restrecord/tpp-ui-input/params/anton-brueckner-transactions.txt"
        );

        LoggedRequest consentInitiateRequest = wireMock
                .findAll(postRequestedFor(urlMatching("/v1/consents.*"))).get(0);
        redirectOkUri = consentInitiateRequest.getHeader(TPP_REDIRECT_URI);
        return self();
    }

    @SneakyThrows
    public AccountListRequest open_banking_user_max_musterman_provided_initial_parameters_to_list_accounts() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
                "mockedsandbox/restrecord/tpp-ui-input/params/max-musterman-account.txt"
        );
        return self();
    }

    @SneakyThrows
    public AccountListRequest open_banking_user_max_musterman_provided_initial_parameters_to_list_transactions() {
        provideParametersToBankingProtocol(
                PARAMETERS_PROVIDE_MORE,
                "mockedsandbox/restrecord/tpp-ui-input/params/max-musterman-transactions.txt"
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
    public AccountListRequest open_banking_user_max_musterman_selected_sca_challenge_type_email1() {
        provideParametersToBankingProtocol(
                "/v1/parameters/select-sca-method/",
                "mockedsandbox/restrecord/tpp-ui-input/params/max-musterman-selected-sca-email1.txt"
        );
        return self();
    }

    @SneakyThrows
    public AccountListRequest open_banking_user_max_musterman_selected_sca_challenge_type_email2() {
        provideParametersToBankingProtocol(
                "/v1/parameters/select-sca-method/",
                "mockedsandbox/restrecord/tpp-ui-input/params/max-musterman-selected-sca-email2.txt"
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
                .andDo(print())
                .andExpect(matcher)
                .andDo(mvcResult -> redirectUriToGetUserParams = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION))
                .andReturn();
        responseContent = result.getResponse().getContentAsString();
    }

    private void updateExecutionId() {
        log.info("Parsing {} to get execution id", redirectUriToGetUserParams);
        execId = Iterables.getLast(
                Splitter.on("/").split(Splitter.on("?").split(redirectUriToGetUserParams).iterator().next())
        );
    }
}

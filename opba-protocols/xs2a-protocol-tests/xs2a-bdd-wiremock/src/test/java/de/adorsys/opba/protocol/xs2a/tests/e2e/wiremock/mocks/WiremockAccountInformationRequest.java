package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationRequestCommon;
import org.awaitility.Durations;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_REDIRECT_URI;
import static org.awaitility.Awaitility.await;

@JGivenStage
public class WiremockAccountInformationRequest<SELF extends WiremockAccountInformationRequest<SELF>> extends AccountInformationRequestCommon<SELF> {

    @ExpectedScenarioState
    private WireMockServer wireMock;

    public SELF open_banking_redirect_uri_extracted() {
        LoggedRequest consentInitiateRequest = await().atMost(Durations.TEN_SECONDS)
                .until(() ->
                        wireMock.findAll(postRequestedFor(urlMatching("/v1/consents.*"))), it -> !it.isEmpty()
                ).get(0);

        this.redirectOkUri = consentInitiateRequest.getHeader(TPP_REDIRECT_URI);
        return self();
    }
}

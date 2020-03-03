package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationRequestCommon;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.awaitility.Durations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_REDIRECT_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@JGivenStage
public class WiremockAccountInformationRequest<SELF extends WiremockAccountInformationRequest<SELF>> extends AccountInformationRequestCommon<SELF> {

    @ExpectedScenarioState
    private WireMockServer wireMock;

    public SELF open_banking_redirect_from_aspsp_ok_webhook_called() {
        LoggedRequest consentInitiateRequest = await().atMost(Durations.TEN_SECONDS)
                .until(() ->
                        wireMock.findAll(postRequestedFor(urlMatching("/v1/consents.*"))), it -> !it.isEmpty()
                ).get(0);

        this.redirectOkUri = consentInitiateRequest.getHeader(TPP_REDIRECT_URI);
        ExtractableResponse<Response> response = RestAssured
                .when()
                    .get(redirectOkUri)
                .then()
                    .statusCode(HttpStatus.SEE_OTHER.value())
                    .extract();

        assertThat(response.header(HttpHeaders.LOCATION)).contains("localhost");

        return self();
    }
}

package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentRequestCommon;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.awaitility.Durations;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withSignatureHeaders;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_REDIRECT_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.HttpHeaders.LOCATION;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class WiremockPaymentRequest<SELF extends WiremockPaymentRequest<SELF>> extends PaymentRequestCommon<SELF> {

    @ExpectedScenarioState
    private WireMockServer wireMock;

    public SELF open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test() {
        LoggedRequest paymentInitiateRequest = await().atMost(Durations.TEN_SECONDS)
                                                       .until(() ->
                                                                      wireMock.findAll(postRequestedFor(urlMatching("/v1/payments/sepa-credit-transfers.*"))), it -> !it.isEmpty()
                                                       ).get(0);

        this.redirectOkUri = paymentInitiateRequest.getHeader(TPP_REDIRECT_URI);
        ExtractableResponse<Response> response = withSignatureHeaders(RestAssured
                        .given()
                              .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie), requestSigningService)
                        .when()
                            .get(redirectOkUri)
                        .then()
                             .statusCode(HttpStatus.SEE_OTHER.value())
                             .extract();

        assertThat(response.header(LOCATION)).contains("pis").contains("consent-result");

        return self();
    }

    public SELF open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test_without_cookie_unauthorized() {
        LoggedRequest paymentInitiateRequest = await().atMost(Durations.TEN_SECONDS)
                                                       .until(() ->
                                                                      wireMock.findAll(postRequestedFor(urlMatching("/v1/payments/sepa-credit-transfers.*"))), it -> !it.isEmpty()
                                                       ).get(0);

        this.redirectOkUri = paymentInitiateRequest.getHeader(TPP_REDIRECT_URI);
        withSignatureHeaders(RestAssured
                                        .given(), requestSigningService)
                                        .when()
                                                .get(redirectOkUri)
                                        .then()
                                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                                .extract();

        return self();
    }

}

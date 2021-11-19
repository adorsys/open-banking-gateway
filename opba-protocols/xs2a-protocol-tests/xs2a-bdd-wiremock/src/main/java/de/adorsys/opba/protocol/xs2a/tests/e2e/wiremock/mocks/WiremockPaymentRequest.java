package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.LocationExtractorUtil;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AdminUtil;
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
import static de.adorsys.xs2a.adapter.api.RequestHeaders.TPP_REDIRECT_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class WiremockPaymentRequest<SELF extends WiremockPaymentRequest<SELF>> extends PaymentRequestCommon<SELF> {

    @ExpectedScenarioState
    private WireMockServer wireMock;

    public SELF open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test() {
        return open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test(0);
    }

    public SELF open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test(int paymentCreationRequestIndex) {
        extractRedirectOkUriSentByOpbaFromWiremock(paymentCreationRequestIndex);
        ExtractableResponse<Response> response = withSignatureHeaders(RestAssured
                        .given()
                              .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie))
                        .when()
                            .get(redirectOkUri)
                        .then()
                             .statusCode(HttpStatus.ACCEPTED.value())
                             .extract();

        assertThat(LocationExtractorUtil.getLocation(response)).contains("pis").contains("consent-result");

        return self();
    }

    public SELF open_banking_redirect_from_aspsp_ok_webhook_called_for_api_test_without_cookie_unauthorized() {
        extractRedirectOkUriSentByOpbaFromWiremock();
        withSignatureHeaders(RestAssured
                                        .given())
                                        .when()
                                                .get(redirectOkUri)
                                        .then()
                                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                                .extract();

        return self();
    }

    public SELF open_banking_redirect_from_aspsp_with_static_oauth2_code_to_exchange_to_token(String code) {
        extractRedirectOkUriSentByOpbaFromWiremock();
        ExtractableResponse<Response> response = RestAssured
                .given()
                    .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                .when()
                    .get(redirectOkUri + "?code=" + code)
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                .extract();

        updateRedirectCode(response);
        return self();
    }

    public SELF current_redirected_to_screen_is_payment_result() {
        assertThat(this.redirectUriToGetUserParams).contains("pis").contains("consent-result");
        return self();
    }

    public SELF admin_calls_delete_bank(String bankUuid) {
        AdminUtil.adminCallsDeleteBank(bankUuid);
        return self();
    }

    private void extractRedirectOkUriSentByOpbaFromWiremock() {
        extractRedirectOkUriSentByOpbaFromWiremock(0);
    }

    private void extractRedirectOkUriSentByOpbaFromWiremock(int paymentCreationRequestIndex) {
        LoggedRequest paymentInitiateRequest = await().atMost(Durations.TEN_SECONDS)
                .until(() ->
                        wireMock.findAll(postRequestedFor(urlMatching("/v1/payments/sepa-credit-transfers.*"))), it -> !it.isEmpty()
                ).get(paymentCreationRequestIndex);
        this.redirectOkUri = paymentInitiateRequest.getHeader(TPP_REDIRECT_URI);
    }

}

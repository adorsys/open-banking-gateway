package de.adorsys.opba.starter;

import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.api.security.generator.api.DataToSignProvider;
import de.adorsys.opba.api.security.generator.api.RequestToSign;
import de.adorsys.opba.api.security.requestsigner.OpenBankingDataToSignProvider;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.xs2a.entrypoint.ais.Xs2aListAccountsEntrypoint;
import de.adorsys.opba.protocol.xs2a.entrypoint.ais.Xs2aSandboxListTransactionsEntrypoint;
import de.adorsys.opba.protocol.xs2a.entrypoint.pis.Xs2aGetPaymentStatusEntrypoint;
import de.adorsys.opba.protocol.xs2a.entrypoint.pis.Xs2aInitiateSinglePaymentEntrypoint;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.CommonGivenStages;
import de.adorsys.opba.starter.config.FintechRequestSigningTestConfig;
import de.adorsys.xs2a.adapter.api.model.PaymentProduct;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_SIGNATURE;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentStagesCommonUtil.withPaymentHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AIS_ACCOUNTS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AIS_TRANSACTIONS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.ANTON_BRUECKNER;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.PIS_PAYMENT_STATUS_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.PIS_SINGLE_PAYMENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withAccountsHeaders;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.withTransactionsHeaders;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * This is a very basic test to ensure application starts up and components are bundled properly.
 * Protocols are tested in their own packages exhaustively.
 */
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = {OpenBankingEmbeddedApplication.class, FintechRequestSigningTestConfig.class}, webEnvironment = RANDOM_PORT)
class BasicOpenBankingStartupTest {

    @SpyBean
    private Xs2aListAccountsEntrypoint xs2aListAccountsEntrypoint;

    @SpyBean
    private Xs2aSandboxListTransactionsEntrypoint xs2aListTransactionsEntrypoint;

    @SpyBean
    private Xs2aInitiateSinglePaymentEntrypoint xs2aInitiateSinglePaymentEntrypoint;

    @MockBean
    private Xs2aGetPaymentStatusEntrypoint xs2aGetPaymentStatusEntrypoint;

    @Autowired
    private RequestSigningService signingService;

    @LocalServerPort
    private int serverPort;

    @BeforeEach
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost:" + serverPort;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        config = config().redirect(redirectConfig().followRedirects(false));
        RestAssured.replaceFiltersWith(new CommonGivenStages.RequestSigner(signingService, new OpenBankingDataToSignProvider()));
    }

    @Test
    void testAppStartsUp() {
        // NOP - just test that context loads OK
    }

    @Test
    void testXs2aProtocolIsWiredForSandboxAccountList() {
        xs2aAccountList(HttpStatus.ACCEPTED);

        verify(xs2aListAccountsEntrypoint).execute(any());
    }

    @Test
    void testXs2aProtocolIsWiredForSandboxTransactionList() {
        xs2aTransactionList(HttpStatus.ACCEPTED);

        verify(xs2aListTransactionsEntrypoint).execute(any());
    }

    @Test
    void testXs2aProtocolIsWiredForPaymentsGetStatus() {
        xs2aPaymentStatusGet(HttpStatus.OK);

        verify(xs2aGetPaymentStatusEntrypoint).execute(any());
    }

    @Test
    void testXs2aProtocolIsWiredForPayments() {
        xs2aPaymentCreate(HttpStatus.ACCEPTED);

        verify(xs2aInitiateSinglePaymentEntrypoint).execute(any());
    }

    @Test
    void testCorrectSignatureRequiredForSandboxAccountList() {
        RestAssured.replaceFiltersWith(new SignatureHashTamperer(signingService, new OpenBankingDataToSignProvider()));
        xs2aAccountList(HttpStatus.NOT_FOUND);

        RestAssured.replaceFiltersWith(new NaiveSignatureTamperer(signingService, new OpenBankingDataToSignProvider()));
        xs2aAccountList(HttpStatus.NOT_FOUND);

        verify(xs2aListAccountsEntrypoint, never()).execute(any());
    }

    @Test
    void testCorrectSignatureRequiredForTransactionList() {
        RestAssured.replaceFiltersWith(new SignatureHashTamperer(signingService, new OpenBankingDataToSignProvider()));
        xs2aTransactionList(HttpStatus.NOT_FOUND);

        RestAssured.replaceFiltersWith(new NaiveSignatureTamperer(signingService, new OpenBankingDataToSignProvider()));
        xs2aTransactionList(HttpStatus.NOT_FOUND);

        verify(xs2aListTransactionsEntrypoint, never()).execute(any());
    }

    @Test
    void testCorrectSignatureRequiredForPaymentsGetStatus() {
        RestAssured.replaceFiltersWith(new SignatureHashTamperer(signingService, new OpenBankingDataToSignProvider()));
        xs2aPaymentStatusGet(HttpStatus.NOT_FOUND);

        RestAssured.replaceFiltersWith(new NaiveSignatureTamperer(signingService, new OpenBankingDataToSignProvider()));
        xs2aPaymentStatusGet(HttpStatus.NOT_FOUND);

        verify(xs2aGetPaymentStatusEntrypoint, never()).execute(any());
    }

    @Test
    void testCorrectSignatureRequiredForPayments() {
        RestAssured.replaceFiltersWith(new SignatureHashTamperer(signingService, new OpenBankingDataToSignProvider()));
        xs2aPaymentCreate(HttpStatus.NOT_FOUND);

        RestAssured.replaceFiltersWith(new NaiveSignatureTamperer(signingService, new OpenBankingDataToSignProvider()));
        xs2aPaymentCreate(HttpStatus.NOT_FOUND);

        verify(xs2aInitiateSinglePaymentEntrypoint, never()).execute(any());
    }

    private void xs2aAccountList(HttpStatus expected) {
        withAccountsHeaders(ANTON_BRUECKNER)
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                .when()
                    .get(AIS_ACCOUNTS_ENDPOINT)
                .then()
                    .statusCode(expected.value());
    }

    private void xs2aTransactionList(HttpStatus expected) {
        withTransactionsHeaders(ANTON_BRUECKNER)
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                .when()
                    .get(AIS_TRANSACTIONS_ENDPOINT, "ACCOUNT-1")
                .then()
                    .statusCode(expected.value());
    }

    private void xs2aPaymentStatusGet(HttpStatus expected) {
        doReturn(CompletableFuture.completedFuture(new SuccessResult<>(getPaymentStatusBody())))
                .when(xs2aGetPaymentStatusEntrypoint).execute(any());

        withPaymentHeaders(ANTON_BRUECKNER)
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                .when()
                    .contentType(ContentType.JSON.withCharset(StandardCharsets.UTF_8))
                    .get(PIS_PAYMENT_STATUS_ENDPOINT, PaymentProduct.SEPA_CREDIT_TRANSFERS.toString())
                .then()
                    .statusCode(expected.value());
    }

    private void xs2aPaymentCreate(HttpStatus expected) {
        withPaymentHeaders(ANTON_BRUECKNER)
                    .header(SERVICE_SESSION_ID, UUID.randomUUID().toString())
                .when()
                    .contentType(ContentType.JSON.withCharset(StandardCharsets.UTF_8))
                    .body(getPaymentBodyStub())
                    .post(PIS_SINGLE_PAYMENT_ENDPOINT, PaymentProduct.SEPA_CREDIT_TRANSFERS.toString())
                .then()
                    .statusCode(expected.value());
    }


    @SneakyThrows
    private String getPaymentBodyStub() {
        return Resources.asCharSource(Resources.getResource("anton-brueckner-single-sepa-payment.json"), StandardCharsets.UTF_8).read();
    }

    private PaymentStatusBody getPaymentStatusBody() {
        var paymentStatusBody = new PaymentStatusBody();
        paymentStatusBody.setTransactionStatus("ACSP");
        paymentStatusBody.setCreatedAt(OffsetDateTime.now());
        return paymentStatusBody;
    }

    @RequiredArgsConstructor
    public static class SignatureHashTamperer implements Filter {

        private final RequestSigningService signingService;
        private final DataToSignProvider dataToSignProvider;

        @Override
        public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
            RequestToSign toSign = computeRequestToSign(requestSpec);

            String signature = dataToSignProvider.normalizerFor(toSign).canonicalStringToSign(toSign) + "1234";
            requestSpec = requestSpec.replaceHeader(X_REQUEST_SIGNATURE, signingService.signature(signature));

            return ctx.next(requestSpec, responseSpec);
        }
    }

    @RequiredArgsConstructor
    public static class NaiveSignatureTamperer implements Filter {

        private final RequestSigningService signingService;
        private final DataToSignProvider dataToSignProvider;

        @Override
        public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {

            RequestToSign toSign = computeRequestToSign(requestSpec);

            String signature = dataToSignProvider.normalizerFor(toSign).canonicalStringToSign(toSign);
            requestSpec = requestSpec.replaceHeader(X_REQUEST_SIGNATURE, signingService.signature(signature).replaceAll("A", "B"));

            return ctx.next(requestSpec, responseSpec);
        }
    }

    private static RequestToSign computeRequestToSign(FilterableRequestSpecification requestSpec) {
        Map<String, String> headers = requestSpec.getHeaders().asList().stream()
                .collect(Collectors.toMap(Header::getName, Header::getValue, (old, newer) -> newer, HashMap::new));
        return RequestToSign.builder()
                .method(DataToSignProvider.HttpMethod.valueOf(requestSpec.getMethod()))
                .path(requestSpec.getDerivedPath())
                .headers(headers)
                .queryParams(requestSpec.getQueryParams())
                .body(requestSpec.getBody())
                .build();
    }
}

package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.tppbankingapi.orchestrated.pis.model.generated.PaymentInitiation;
import de.adorsys.opba.tppbankingapi.orchestrated.pis.resource.generated.TppBankingApiSinglePaymentPisApi;
import de.adorsys.opba.tppbankingapi.service.PaymentOrchestratedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class TppBankingApiOrchestratedPayment implements TppBankingApiSinglePaymentPisApi {
    private final PaymentOrchestratedService payments;

    @Override
    public CompletableFuture initiatePayment(String fintechUserID,
                                                                          String fintechRedirectURLOK,
                                                                          String fintechRedirectURLNOK,
                                                                          UUID xRequestID,
                                                                          String paymentProduct,
                                                                          PaymentInitiation body,
                                                                          String xTimestampUTC,
                                                                          String xRequestSignature,
                                                                          String fintechID,
                                                                          String serviceSessionPassword,
                                                                          String fintechDataPassword,
                                                                          UUID bankProfileID,
                                                                          Boolean xPsuAuthenticationRequired,
                                                                          String xProtocolConfiguration,
                                                                          Boolean computePSUIPAddress,
                                                                          String psUIPAddress,
                                                                          Boolean fintechDecoupledPreferred,
                                                                          String fintechBrandLoggingInformation,
                                                                          String fintechNotificationURI,
                                                                          String fintechNotificationContentPreferred) {
        return payments.initiatePayment(fintechUserID,
                fintechRedirectURLOK,
                fintechRedirectURLNOK,
                xRequestID,
                paymentProduct,
                body,
                xTimestampUTC,
                xRequestSignature,
                fintechID,
                serviceSessionPassword,
                fintechDataPassword,
                bankProfileID,
                xPsuAuthenticationRequired,
                xProtocolConfiguration,
                computePSUIPAddress,
                psUIPAddress,
                fintechDecoupledPreferred,
                fintechBrandLoggingInformation,
                fintechNotificationURI,
                fintechNotificationContentPreferred);
    }
}

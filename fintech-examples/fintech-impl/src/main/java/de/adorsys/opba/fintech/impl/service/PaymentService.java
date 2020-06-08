package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.fintech.api.model.generated.SinglePaymentInitiationRequest;
import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import de.adorsys.opba.fintech.impl.tppclients.TppPisClient;
import de.adorsys.opba.tpp.pis.api.model.generated.AccountReference;
import de.adorsys.opba.tpp.pis.api.model.generated.Amount;
import de.adorsys.opba.tpp.pis.api.model.generated.PaymentInitiation;
import de.adorsys.opba.tpp.pis.api.model.generated.PaymentInitiationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_FINTECH_ID;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_REQUEST_SIGNATURE;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_TIMESTAMP_UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    // FIXME: https://github.com/adorsys/open-banking-gateway/issues/316
    private String currency = "EUR";
    private String paymentProduct = "sepa-credit-transfers";

    private final TppPisClient tppPisClient;
    private final TppProperties tppProperties;
    private final RestRequestContext restRequestContext;
    private final SessionLogicService sessionLogicService;

    public ResponseEntity<Void> initiateSinglePayment(String bankId,
                                                      SinglePaymentInitiationRequest singlePaymentInitiationRequest,
                                                      String okUrl,
                                                      String notOkUrl) {
        log.info("fill paramemeters for payment");
        SessionEntity sessionEntity = sessionLogicService.getSession();
        PaymentInitiation payment = new PaymentInitiation();
        // creditor
        AccountReference creditorAccount = new AccountReference();
        creditorAccount.setIban(singlePaymentInitiationRequest.getCreditorIban());
        payment.setCreditorAccount(creditorAccount);
        // debitor
        AccountReference debitorAccount = new AccountReference();
        debitorAccount.setIban(singlePaymentInitiationRequest.getDebitorIban());
        payment.setDebtorAccount(debitorAccount);
        // name
        payment.setCreditorName(singlePaymentInitiationRequest.getName());
        // amount
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setAmount(singlePaymentInitiationRequest.getAmount());
        payment.setInstructedAmount(amount);
        // purpose
        payment.endToEndIdentification(singlePaymentInitiationRequest.getPurpose());
        log.info("start call for payment {} {}", okUrl, notOkUrl);
        ResponseEntity<PaymentInitiationResponse> responseOfTpp = tppPisClient.initiatePayment(
                payment,
                tppProperties.getServiceSessionPassword(),
                sessionEntity.getUserEntity().getFintechUserId(),
                okUrl, notOkUrl,
                UUID.fromString(restRequestContext.getRequestId()),
                paymentProduct, COMPUTE_X_TIMESTAMP_UTC,
                // TODO has to be PIS
                OperationType.AIS.toString(),
                COMPUTE_X_REQUEST_SIGNATURE,
                COMPUTE_FINTECH_ID, bankId, null
        );
        if (responseOfTpp.getStatusCode() != HttpStatus.ACCEPTED) {
            throw new RuntimeException("Did expect status 202 from tpp, but got " + responseOfTpp.getStatusCodeValue());
        }
        log.info("finished call for payment {}", responseOfTpp.getStatusCodeValue());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(responseOfTpp.getHeaders().getLocation());
        log.info("redirection to {}", httpHeaders.getLocation());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.ACCEPTED);
    }

}

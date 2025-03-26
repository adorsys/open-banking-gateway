package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.PaymentInitiationWithStatusResponse;
import de.adorsys.opba.fintech.api.model.generated.SinglePaymentInitiationRequest;
import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.PaymentEntity;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.PaymentRepository;
import de.adorsys.opba.fintech.impl.mapper.PaymentInitiationWithStatusResponseMapper;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import de.adorsys.opba.fintech.impl.tppclients.ConsentType;
import de.adorsys.opba.fintech.impl.tppclients.TppPisPaymentStatusClient;
import de.adorsys.opba.fintech.impl.tppclients.TppPisSinglePaymentClient;
import de.adorsys.opba.tpp.pis.api.model.generated.AccountReference;
import de.adorsys.opba.tpp.pis.api.model.generated.Amount;
import de.adorsys.opba.tpp.pis.api.model.generated.PaymentInformationResponse;
import de.adorsys.opba.tpp.pis.api.model.generated.PaymentInitiation;
import de.adorsys.opba.tpp.pis.api.model.generated.PaymentInitiationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_FINTECH_ID;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_REQUEST_SIGNATURE;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_TIMESTAMP_UTC;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.HEADER_COMPUTE_PSU_IP_ADDRESS;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    // FIXME: https://github.com/adorsys/open-banking-gateway/issues/316
    private static final String SEPA_PAYMENT_PRODUCT = "sepa-credit-transfers";
    private static final String INSTANT_SEPA_PAYMENT_PRODUCT = "instant-sepa-credit-transfers";
    private static final String CURRENCY = "EUR";

    private final TppPisSinglePaymentClient tppPisSinglePaymentClient;
    private final TppPisPaymentStatusClient tppPisPaymentStatusClient;
    private final TppProperties tppProperties;
    private final RestRequestContext restRequestContext;
    private final SessionLogicService sessionLogicService;
    private final FintechUiConfig uiConfig;
    private final RedirectHandlerService redirectHandlerService;
    private final HandleAcceptedService handleAcceptedService;
    private final PaymentRepository paymentRepository;

    public ResponseEntity<Void> initiateSinglePayment(String bankProfileId, String accountId, SinglePaymentInitiationRequest singlePaymentInitiationRequest,
                                                      String fintechOkUrl, String fintechNOkUrl, Boolean xPisPsuAuthenticationRequired, Boolean fintechDecoupledPreferred,
                                                      String fintechBrandLoggingInformation, String fintechNotificationURI, String fintechRedirectNotificationContentPreferred) {
        log.info("fill paramemeters for payment");
        final String fintechRedirectCode = UUID.randomUUID().toString();
        SessionEntity sessionEntity = sessionLogicService.getSession();
        PaymentInitiation payment = new PaymentInitiation();
        payment.setCreditorAccount(getAccountReference(singlePaymentInitiationRequest.getCreditorIban()));
        payment.setDebtorAccount(getAccountReference(singlePaymentInitiationRequest.getDebitorIban()));
        payment.setCreditorName(singlePaymentInitiationRequest.getName());
        payment.setInstructedAmount(getAmountWithCurrency(singlePaymentInitiationRequest.getAmount()));
        payment.remittanceInformationUnstructured(singlePaymentInitiationRequest.getPurpose());
        payment.setEndToEndIdentification(singlePaymentInitiationRequest.getEndToEndIdentification());
        log.info("start call for payment {} {}", fintechOkUrl, fintechNOkUrl);
        var paymentProduct = singlePaymentInitiationRequest.isInstantPayment() ? INSTANT_SEPA_PAYMENT_PRODUCT : SEPA_PAYMENT_PRODUCT;
        ResponseEntity<PaymentInitiationResponse> responseOfTpp = tppPisSinglePaymentClient.initiatePayment(
                sessionEntity.getUserEntity().getFintechUserId(),
                RedirectUrlsEntity.buildPaymentOkUrl(uiConfig, fintechRedirectCode),
                RedirectUrlsEntity.buildPaymentNokUrl(uiConfig, fintechRedirectCode),
                UUID.fromString(restRequestContext.getRequestId()),
                paymentProduct,
                payment,
                COMPUTE_X_TIMESTAMP_UTC,
                COMPUTE_X_REQUEST_SIGNATURE,
                COMPUTE_FINTECH_ID,
                null,
                tppProperties.getFintechDataProtectionPassword(),
                UUID.fromString(bankProfileId),
                xPisPsuAuthenticationRequired,
                null,
                HEADER_COMPUTE_PSU_IP_ADDRESS,
                null,
                fintechDecoupledPreferred, fintechBrandLoggingInformation,
                fintechNotificationURI, fintechRedirectNotificationContentPreferred);
        if (responseOfTpp.getStatusCode() != HttpStatus.ACCEPTED) {
            throw new RuntimeException("Did expect status 202 from tpp, but got " + responseOfTpp.getStatusCodeValue());
        }
        redirectHandlerService.registerRedirectStateForSession(fintechRedirectCode, fintechOkUrl, fintechNOkUrl);
        return handleAcceptedService.handleAccepted(paymentRepository, ConsentType.PIS, bankProfileId, accountId, fintechRedirectCode, sessionEntity,
                responseOfTpp.getHeaders(), paymentProduct);
    }

    public ResponseEntity<List<PaymentInitiationWithStatusResponse>> retrieveAllSinglePayments(String bankProfileID, String accountId) {
        SessionEntity sessionEntity = sessionLogicService.getSession();
        // TODO https://app.zenhub.com/workspaces/open-banking-gateway-5dd3b3daf010250001260675/issues/adorsys/open-banking-gateway/812
        // TODO https://app.zenhub.com/workspaces/open-banking-gateway-5dd3b3daf010250001260675/issues/adorsys/open-banking-gateway/794
        List<PaymentEntity> payments = paymentRepository.findByUserEntityAndBankIdAndAccountIdAndPaymentConfirmed(sessionEntity.getUserEntity(), bankProfileID, accountId, true);
        if (payments.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        List<PaymentInitiationWithStatusResponse> result = new ArrayList<>();

        for (PaymentEntity payment : payments) {
            PaymentInformationResponse body = tppPisPaymentStatusClient.getPaymentInformation(
                    UUID.fromString(restRequestContext.getRequestId()),
                    payment.getPaymentProduct(),
                    COMPUTE_X_TIMESTAMP_UTC,
                    COMPUTE_X_REQUEST_SIGNATURE,
                    COMPUTE_FINTECH_ID,
                    null,
                    tppProperties.getFintechDataProtectionPassword(),
                    UUID.fromString(bankProfileID),
                    payment.getTppServiceSessionId()).getBody();
            PaymentInitiationWithStatusResponse paymentInitiationWithStatusResponse = Mappers
                    .getMapper(PaymentInitiationWithStatusResponseMapper.class)
                    .mapFromTppToFintech(body);
            result.add(paymentInitiationWithStatusResponse);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private AccountReference getAccountReference(String iban) {
        AccountReference account = new AccountReference();
        account.setCurrency(CURRENCY);
        account.setIban(iban);
        return account;
    }

    private Amount getAmountWithCurrency(String amountWihthoutCurrency) {
        Amount amount = new Amount();
        amount.setCurrency(CURRENCY);
        amount.setAmount(amountWihthoutCurrency);
        return amount;
    }

}

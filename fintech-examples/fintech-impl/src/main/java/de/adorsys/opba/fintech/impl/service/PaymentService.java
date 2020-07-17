package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.fintech.api.model.generated.PaymentInitiationWithStatusResponse;
import de.adorsys.opba.fintech.api.model.generated.SinglePaymentInitiationRequest;
import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.ConsentEntity;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.ConsentRepository;
import de.adorsys.opba.fintech.impl.mapper.PaymentInitiationWithStatusResponseMapper;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import de.adorsys.opba.fintech.impl.tppclients.ConsentType;
import de.adorsys.opba.fintech.impl.tppclients.TppPisPaymentStatusClient;
import de.adorsys.opba.fintech.impl.tppclients.TppPisSinglePaymentClient;
import de.adorsys.opba.tpp.pis.api.model.generated.AccountReference;
import de.adorsys.opba.tpp.pis.api.model.generated.Amount;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    // FIXME: https://github.com/adorsys/open-banking-gateway/issues/316
    private String currency = "EUR";
    private String paymentProduct = "sepa-credit-transfers";

    private final TppPisSinglePaymentClient tppPisSinglePaymentClient;
    private final TppPisPaymentStatusClient tppPisPaymentStatusClient;
    private final TppProperties tppProperties;
    private final RestRequestContext restRequestContext;
    private final SessionLogicService sessionLogicService;
    private final FintechUiConfig uiConfig;
    private final RedirectHandlerService redirectHandlerService;
    private final HandleAcceptedService handleAcceptedService;
    private final ConsentRepository consentRepository;

    public ResponseEntity<Void> initiateSinglePayment(String bankId, String accountId, SinglePaymentInitiationRequest singlePaymentInitiationRequest,
                                                      String fintechOkUrl, String fintechNOkUrl) {
        log.info("fill paramemeters for payment");
        final String fintechRedirectCode = UUID.randomUUID().toString();

        SessionEntity sessionEntity = sessionLogicService.getSession();
        PaymentInitiation payment = new PaymentInitiation();
        payment.setCreditorAccount(getAccountReference(singlePaymentInitiationRequest.getCreditorIban()));
        payment.setDebtorAccount(getAccountReference(singlePaymentInitiationRequest.getDebitorIban()));
        payment.setCreditorName(singlePaymentInitiationRequest.getName());
        payment.setInstructedAmount(getAmountWithCurrency(singlePaymentInitiationRequest.getAmount()));
        payment.remittanceInformationUnstructured(singlePaymentInitiationRequest.getPurpose());
        log.info("start call for payment {} {}", fintechOkUrl, fintechNOkUrl);
        ResponseEntity<PaymentInitiationResponse> responseOfTpp = tppPisSinglePaymentClient.initiatePayment(
                payment,
                tppProperties.getServiceSessionPassword(),
                sessionEntity.getUserEntity().getFintechUserId(),
                RedirectUrlsEntity.buildOkUrl(uiConfig, fintechRedirectCode),
                RedirectUrlsEntity.buildNokUrl(uiConfig, fintechRedirectCode),
                UUID.fromString(restRequestContext.getRequestId()),
                paymentProduct,
                COMPUTE_X_TIMESTAMP_UTC,
                OperationType.PIS.toString(),
                COMPUTE_X_REQUEST_SIGNATURE,
                COMPUTE_FINTECH_ID,
                bankId,
                false);
        if (responseOfTpp.getStatusCode() != HttpStatus.ACCEPTED) {
            throw new RuntimeException("Did expect status 202 from tpp, but got " + responseOfTpp.getStatusCodeValue());
        }
        redirectHandlerService.registerRedirectStateForSession(fintechRedirectCode, fintechOkUrl, fintechNOkUrl);
        return handleAcceptedService.handleAccepted(consentRepository, ConsentType.PIS, bankId, accountId, fintechRedirectCode, sessionEntity,
                responseOfTpp.getHeaders());
    }

    public ResponseEntity<List<PaymentInitiationWithStatusResponse>> retrieveAllSinglePayments(String bankId, String accountId) {
        SessionEntity sessionEntity = sessionLogicService.getSession();
        // TODO https://app.zenhub.com/workspaces/open-banking-gateway-5dd3b3daf010250001260675/issues/adorsys/open-banking-gateway/812
        // TODO https://app.zenhub.com/workspaces/open-banking-gateway-5dd3b3daf010250001260675/issues/adorsys/open-banking-gateway/794
        List<ConsentEntity> list = consentRepository.findByUserEntityAndBankIdAndAccountIdAndConsentConfirmed(sessionEntity.getUserEntity(), bankId, accountId, true);
        List<PaymentInitiationWithStatusResponse> result = new ArrayList<>();

        for (ConsentEntity consent : list) {
            de.adorsys.opba.tpp.pis.api.model.generated.PaymentInitiationWithStatusResponse body = tppPisPaymentStatusClient.getPaymentInformation(tppProperties.getServiceSessionPassword(),
                    sessionEntity.getUserEntity().getFintechUserId(),
                    UUID.fromString(restRequestContext.getRequestId()),
                    paymentProduct,
                    COMPUTE_X_TIMESTAMP_UTC,
                    OperationType.PIS.toString(),
                    COMPUTE_X_REQUEST_SIGNATURE,
                    COMPUTE_FINTECH_ID,
                    bankId,
                    consent.getTppServiceSessionId()).getBody();
            PaymentInitiationWithStatusResponse paymentInitiationWithStatusResponse = Mappers.getMapper(PaymentInitiationWithStatusResponseMapper.class).mapFromTppToFintech(body);
            paymentInitiationWithStatusResponse.setInitiationDate(consent.getCreationTime().toLocalDate());
            result.add(paymentInitiationWithStatusResponse);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    private AccountReference getAccountReference(String iban) {
        AccountReference account = new AccountReference();
        account.setIban(iban);
        return account;
    }

    private Amount getAmountWithCurrency(String amountWihthoutCurrency) {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setAmount(amountWihthoutCurrency);
        return amount;
    }

}

package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.LoTRetrievalInformation;
import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.ConsentEntity;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.ConsentRepository;
import de.adorsys.opba.fintech.impl.mapper.ManualMapper;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import de.adorsys.opba.fintech.impl.tppclients.ConsentType;
import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_FINTECH_ID;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_REQUEST_SIGNATURE;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_TIMESTAMP_UTC;


@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final FintechUiConfig uiConfig;
    private final TppAisClient tppAisClient;
    private final RestRequestContext restRequestContext;
    private final TppProperties tppProperties;
    private final RedirectHandlerService redirectHandlerService;
    private final ConsentRepository consentRepository;
    private final HandleAcceptedService handleAcceptedService;
    private final ConsentService consentService;

    public ResponseEntity listTransactions(SessionEntity sessionEntity, String fintechOkUrl, String fintechNOkUrl, String bankId,
                                           String accountId, LocalDate dateFrom, LocalDate dateTo, String entryReferenceFrom,
                                           String bookingStatus, Boolean deltaList, LoTRetrievalInformation loTRetrievalInformation) {

        log.info("LoT {}", loTRetrievalInformation);
        if (loTRetrievalInformation.equals(LoTRetrievalInformation.fromTppWithNewConsent)) {
            consentService.deleteConsent(sessionEntity.getUserEntity(), ConsentType.AIS, bankId);
        }

        String fintechRedirectCode = UUID.randomUUID().toString();
        Optional<ConsentEntity> optionalConsent = consentRepository.findByUserEntityAndBankIdAndConsentTypeAndConsentConfirmed(sessionEntity.getUserEntity(),
                bankId, ConsentType.AIS, Boolean.TRUE);
        if (optionalConsent.isPresent()) {
            log.info("LoT found valid ais consent for user {} bank {}", sessionEntity.getUserEntity().getLoginUserName(), bankId);
        } else {
            log.info("LoT no valid ais consent for user {} bank {} available", sessionEntity.getUserEntity().getLoginUserName(), bankId);
        }

        ResponseEntity<TransactionsResponse> transactions = tppAisClient.getTransactions(
                accountId, tppProperties.getServiceSessionPassword(),
                sessionEntity.getUserEntity().getLoginUserName(),
                RedirectUrlsEntity.buildOkUrl(uiConfig, fintechRedirectCode),
                RedirectUrlsEntity.buildNokUrl(uiConfig, fintechRedirectCode),
                UUID.fromString(restRequestContext.getRequestId()),
                COMPUTE_X_TIMESTAMP_UTC, OperationType.AIS.toString(),
                COMPUTE_X_REQUEST_SIGNATURE,
                COMPUTE_FINTECH_ID, bankId, null, optionalConsent.isPresent() ? optionalConsent.get().getTppServiceSessionId() : null, dateFrom, dateTo,
                entryReferenceFrom, bookingStatus, deltaList);
        switch (transactions.getStatusCode()) {
            case OK:
                return new ResponseEntity<>(ManualMapper.fromTppToFintech(transactions.getBody()), HttpStatus.OK);
            case ACCEPTED:
                log.info("create redirect entity for lot for redirectcode {}", fintechRedirectCode);
                redirectHandlerService.registerRedirectStateForSession(fintechRedirectCode, fintechOkUrl, fintechNOkUrl);
                return handleAcceptedService.handleAccepted(consentRepository, ConsentType.AIS, bankId, fintechRedirectCode, sessionEntity, transactions.getHeaders());
            case UNAUTHORIZED:
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            default:
                throw new RuntimeException("DID NOT EXPECT RETURNCODE:" + transactions.getStatusCode());
        }
    }
}

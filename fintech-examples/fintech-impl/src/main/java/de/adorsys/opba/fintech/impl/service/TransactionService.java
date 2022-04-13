package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.utils.LoTRetrievalInformation;
import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
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
import lombok.SneakyThrows;
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
import static de.adorsys.opba.fintech.impl.tppclients.Consts.HEADER_COMPUTE_PSU_IP_ADDRESS;


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

    @SuppressWarnings("checkstyle:MethodLength") //  FIXME - It is just too many lines of text
    @SneakyThrows
    public ResponseEntity listTransactions(SessionEntity sessionEntity, String fintechOkUrl, String fintechNOkUrl, String bankProfileID,
                                           String accountId, String createConsentIfNone, LocalDate dateFrom, LocalDate dateTo, String entryReferenceFrom,
                                           String bookingStatus, Boolean deltaList, LoTRetrievalInformation loTRetrievalInformation,
                                           Boolean psuAuthenticationRequired, Boolean online) {
        log.info("LoT {}", loTRetrievalInformation);
        String fintechRedirectCode = UUID.randomUUID().toString();
        Optional<ConsentEntity> optionalConsent = Optional.empty();
        if (loTRetrievalInformation.equals(LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT)) {
            optionalConsent = consentRepository.findFirstByUserEntityAndBankIdAndConsentTypeAndConsentConfirmedOrderByCreationTimeDesc(sessionEntity.getUserEntity(),
                bankProfileID, ConsentType.AIS, Boolean.TRUE);
        }
        if (optionalConsent.isPresent()) {
            log.info("LoT found valid {} consent for user {} bank {} from {}",
                optionalConsent.get().getConsentType(), optionalConsent.get().getUserEntity().getLoginUserName(),
                optionalConsent.get().getBankId(), optionalConsent.get().getCreationTime());
        } else {
            log.info("LoT no valid ais consent for user {} bank {} available", sessionEntity.getUserEntity().getLoginUserName(), bankProfileID);
        }
        ResponseEntity<TransactionsResponse> transactions = tppAisClient.getTransactions(
            accountId,
            sessionEntity.getUserEntity().getLoginUserName(),
            RedirectUrlsEntity.buildOkUrl(uiConfig, fintechRedirectCode),
            RedirectUrlsEntity.buildNokUrl(uiConfig, fintechRedirectCode),
            UUID.fromString(restRequestContext.getRequestId()),
            COMPUTE_X_TIMESTAMP_UTC,
            COMPUTE_X_REQUEST_SIGNATURE,
            COMPUTE_FINTECH_ID, null, tppProperties.getFintechDataProtectionPassword(),
            UUID.fromString(bankProfileID),
            psuAuthenticationRequired,
            optionalConsent.map(ConsentEntity::getTppServiceSessionId).orElse(null),
            createConsentIfNone,
            null,
            null,
            HEADER_COMPUTE_PSU_IP_ADDRESS,
            null,
            dateFrom,
            dateTo,
            entryReferenceFrom,
            bookingStatus,
            deltaList,
            online,
            "DISABLED",
            null,
            null
        );
        switch (transactions.getStatusCode()) {
            case OK:
                return new ResponseEntity<>(ManualMapper.fromTppToFintech(transactions.getBody()), HttpStatus.OK);
            case ACCEPTED:
                log.info("create redirect entity for lot for redirectcode {}", fintechRedirectCode);
                redirectHandlerService.registerRedirectStateForSession(fintechRedirectCode, fintechOkUrl, fintechNOkUrl);
                return handleAcceptedService.handleAccepted(consentRepository, ConsentType.AIS, bankProfileID, fintechRedirectCode, sessionEntity, transactions.getHeaders());
            case UNAUTHORIZED:
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            default:
                throw new RuntimeException("DID NOT EXPECT RETURNCODE:" + transactions.getStatusCode());
        }
    }
}

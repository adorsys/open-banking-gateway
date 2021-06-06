package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.utils.LoARetrievalInformation;
import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.ConsentEntity;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.ConsentRepository;
import de.adorsys.opba.fintech.impl.exceptions.ConsentException;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import de.adorsys.opba.fintech.impl.tppclients.Actions;
import de.adorsys.opba.fintech.impl.tppclients.AisErrorDecoder;
import de.adorsys.opba.fintech.impl.tppclients.ConsentType;
import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_FINTECH_ID;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_REQUEST_SIGNATURE;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_TIMESTAMP_UTC;


@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private final FintechUiConfig uiConfig;
    private final TppAisClient tppAisClient;
    private final RestRequestContext restRequestContext;
    private final TppProperties tppProperties;
    private final RedirectHandlerService redirectHandlerService;
    private final ConsentRepository consentRepository;
    private final HandleAcceptedService handleAcceptedService;
    private final BankSearchService searchService;

    public ResponseEntity listAccounts(SessionEntity sessionEntity,
                                       String fintechOkUrl, String fintechNOKUrl,
                                       String bankId, LoARetrievalInformation loARetrievalInformation, boolean withBalance, Boolean psuAuthenticationRequired, Boolean online) {

        log.info("List of accounts {} with balance {}", loARetrievalInformation, withBalance);
        final String fintechRedirectCode = UUID.randomUUID().toString();

        try {
            ResponseEntity accounts = readOpbaResponse(bankId, sessionEntity, fintechRedirectCode, loARetrievalInformation, withBalance, psuAuthenticationRequired, online);

            switch (accounts.getStatusCode()) {
                case OK:
                    return new ResponseEntity<>(accounts.getBody(), HttpStatus.OK);
                case ACCEPTED:
                    log.debug("create redirect entity for redirect code {}", fintechRedirectCode);
                    redirectHandlerService.registerRedirectStateForSession(fintechRedirectCode, fintechOkUrl, fintechNOKUrl);
                    return handleAcceptedService.handleAccepted(consentRepository, ConsentType.AIS, bankId, fintechRedirectCode, sessionEntity, accounts.getHeaders());
                case UNAUTHORIZED:
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                default:
                    throw new RuntimeException("DID NOT EXPECT RETURNCODE:" + accounts.getStatusCode());
            }
        } catch (ConsentException consentException) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(AisErrorDecoder.X_ERROR_CODE, consentException.getXErrorCode());
            return new ResponseEntity(headers, HttpStatus.valueOf(consentException.getHttpResponseCode()));
        }
    }


    private ResponseEntity readOpbaResponse(String bankID, SessionEntity sessionEntity, String redirectCode,
                                            LoARetrievalInformation loARetrievalInformation, boolean withBalance,
                                            Boolean psuAuthenticationRequired, Boolean online) {
        UUID xRequestId = UUID.fromString(restRequestContext.getRequestId());
        Optional<ConsentEntity> optionalConsent = Optional.empty();
        if (loARetrievalInformation.equals(LoARetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT)) {
            optionalConsent = consentRepository.findFirstByUserEntityAndBankIdAndConsentTypeAndConsentConfirmedOrderByCreationTimeDesc(sessionEntity.getUserEntity(),
                bankID, ConsentType.AIS, Boolean.TRUE);
        }
        if (optionalConsent.isPresent()) {
            log.info("LoA found valid {} consent for user {} bank {} from {}",
                optionalConsent.get().getConsentType(),
                optionalConsent.get().getUserEntity().getLoginUserName(),
                optionalConsent.get().getBankId(),
                optionalConsent.get().getCreationTime());
            return consentAvailable(bankID, sessionEntity, redirectCode, xRequestId, optionalConsent, withBalance, psuAuthenticationRequired, online);
        }

        BankProfileResponse bankProfile = searchService.getBankProfileById(bankID).getBody();
        if (null != bankProfile.getBankProfileDescriptor().getConsentSupportByService()
            && "true".equals(bankProfile.getBankProfileDescriptor().getConsentSupportByService().get(Actions.LIST_ACCOUNTS.name()))) {
            log.info("LoA no valid ais consent for user {} bank {} available", sessionEntity.getUserEntity().getLoginUserName(), bankID);
            return consentNotYetAvailable(bankID, sessionEntity, redirectCode, xRequestId, optionalConsent);
        }

        return consentAvailable(bankID, sessionEntity, redirectCode, xRequestId, optionalConsent, withBalance, psuAuthenticationRequired, online);
    }

    private ResponseEntity consentAvailable(String bankID, SessionEntity sessionEntity, String redirectCode,
                                            UUID xRequestId, Optional<ConsentEntity> optionalConsent, boolean withBalance,
                                            Boolean psuAuthenticationRequired, Boolean online) {
        log.info("do LOA for bank {} {} consent", bankID, optionalConsent.isPresent() ? "with" : "without");
        UUID serviceSessionID = optionalConsent.map(ConsentEntity::getTppServiceSessionId).orElse(null);
        return tppAisClient.getAccounts(
            tppProperties.getServiceSessionPassword(),
            sessionEntity.getUserEntity().getFintechUserId(),
            RedirectUrlsEntity.buildOkUrl(uiConfig, redirectCode),
            RedirectUrlsEntity.buildNokUrl(uiConfig, redirectCode),
            xRequestId,
            COMPUTE_X_TIMESTAMP_UTC,
            COMPUTE_X_REQUEST_SIGNATURE,
            COMPUTE_FINTECH_ID,
            bankID,
            psuAuthenticationRequired,
            serviceSessionID,
            withBalance,
            online);
    }

    private ResponseEntity consentNotYetAvailable(String bankID, SessionEntity sessionEntity, String redirectCode, UUID xRequestId, Optional<ConsentEntity> optionalConsent) {
        log.info("do LOT (instead of loa) for bank {} {} consent", bankID, optionalConsent.isPresent() ? "with" : "without");
        UUID serviceSessionID = optionalConsent.map(ConsentEntity::getTppServiceSessionId).orElse(null);
        return tppAisClient.getTransactionsWithoutAccountId(
            tppProperties.getServiceSessionPassword(),
            sessionEntity.getUserEntity().getFintechUserId(),
            RedirectUrlsEntity.buildOkUrl(uiConfig, redirectCode),
            RedirectUrlsEntity.buildNokUrl(uiConfig, redirectCode),
            xRequestId,
            COMPUTE_X_TIMESTAMP_UTC,
            COMPUTE_X_REQUEST_SIGNATURE,
            COMPUTE_FINTECH_ID,
            bankID,
            null,
            serviceSessionID,
            null,
            null,
    null,
        null,
            null
        );
    }
}

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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_FINTECH_ID;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_REQUEST_SIGNATURE;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_TIMESTAMP_UTC;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.HEADER_COMPUTE_PSU_IP_ADDRESS;


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
                                       String bankId, LoARetrievalInformation loARetrievalInformation, String createConsentIfNone,
                                       Boolean fintechDecoupledPreferred, String fintechBrandLoggingInformation, String fintechNotificationURI, String fintechNotificationContentPreferred,
                                       boolean withBalance, Boolean psuAuthenticationRequired, Boolean online) {

        log.info("List of accounts {} with balance {}", loARetrievalInformation, withBalance);
        final String fintechRedirectCode = UUID.randomUUID().toString();

        try {
            ResponseEntity accounts = readOpbaResponse(bankId, sessionEntity, fintechRedirectCode, loARetrievalInformation, createConsentIfNone,
                    fintechDecoupledPreferred, fintechBrandLoggingInformation, fintechNotificationURI, fintechNotificationContentPreferred, withBalance, psuAuthenticationRequired, online);

            switch (accounts.getStatusCode()) {
                case HttpStatus.OK:
                    return new ResponseEntity<>(accounts.getBody(), HttpStatus.OK);
                case HttpStatus.ACCEPTED:
                    log.debug("create redirect entity for redirect code {}", fintechRedirectCode);
                    redirectHandlerService.registerRedirectStateForSession(fintechRedirectCode, fintechOkUrl, fintechNOKUrl);
                    return handleAcceptedService.handleAccepted(consentRepository, ConsentType.AIS, bankId, fintechRedirectCode, sessionEntity, accounts.getHeaders());
                case HttpStatus.UNAUTHORIZED:
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                default:
                    throw new RuntimeException("DID NOT EXPECT RETURNCODE:" + accounts.getStatusCode());
            }
        } catch (ConsentException consentException) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(AisErrorDecoder.X_ERROR_CODE, consentException.getXErrorCode());
            return new ResponseEntity<>(headers, HttpStatus.valueOf(consentException.getHttpResponseCode()));
        }
    }

    @SneakyThrows
    private ResponseEntity readOpbaResponse(String bankID, SessionEntity sessionEntity, String redirectCode, LoARetrievalInformation loARetrievalInformation, String createConsentIfNone,
                                            Boolean fintechDecoupledPreferred, String fintechBrandLoggingInformation, String fintechNotificationURI, String fintechNotificationContentPreferred,
                                            boolean withBalance, Boolean psuAuthenticationRequired, Boolean online) {
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
            return consentAvailable(bankID, sessionEntity, redirectCode, xRequestId, optionalConsent, createConsentIfNone,
                    withBalance, psuAuthenticationRequired, online,
                    fintechDecoupledPreferred, fintechBrandLoggingInformation, fintechNotificationURI, fintechNotificationContentPreferred);
        }

        Map<String, String> consentSupportByService = searchService.getBankProfileById(bankID).getBody().getBankProfileDescriptor().getConsentSupportByService();
        if (null != consentSupportByService && "true".equals(consentSupportByService.get(Actions.LIST_ACCOUNTS.name()))) {
            log.info("LoA no valid ais consent for user {} bank {} available", sessionEntity.getUserEntity().getLoginUserName(), bankID);
            return consentNotYetAvailable(bankID, sessionEntity, redirectCode, xRequestId, psuAuthenticationRequired, optionalConsent, withBalance, online,
                    createConsentIfNone, fintechDecoupledPreferred, fintechBrandLoggingInformation, fintechNotificationURI, fintechNotificationContentPreferred);
        }

        return consentAvailable(bankID, sessionEntity, redirectCode, xRequestId, optionalConsent, createConsentIfNone, withBalance, psuAuthenticationRequired, online,
                fintechDecoupledPreferred, fintechBrandLoggingInformation, fintechNotificationURI, fintechNotificationContentPreferred);
    }

    private ResponseEntity consentAvailable(String bankProfileID, SessionEntity sessionEntity, String redirectCode,
                                            UUID xRequestId, Optional<ConsentEntity> optionalConsent, String createConsentIfNone, boolean withBalance,
                                            Boolean psuAuthenticationRequired, Boolean online,
                                            Boolean fintechDecoupledPreferred, String fintechBrandLoggingInformation, String fintechNotificationURI, String fintechNotificationContentPreferred) {
        log.info("do LOA for bank {} {} consent", bankProfileID, optionalConsent.isPresent() ? "with" : "without");
        UUID serviceSessionID = optionalConsent.map(ConsentEntity::getTppServiceSessionId).orElse(null);
        return tppAisClient.getAccounts(
                sessionEntity.getUserEntity().getFintechUserId(),
                RedirectUrlsEntity.buildOkUrl(uiConfig, redirectCode),
                RedirectUrlsEntity.buildNokUrl(uiConfig, redirectCode),
                xRequestId,
                COMPUTE_X_TIMESTAMP_UTC,
                COMPUTE_X_REQUEST_SIGNATURE,
                COMPUTE_FINTECH_ID,
                null,
                tppProperties.getFintechDataProtectionPassword(),
                UUID.fromString(bankProfileID),
                psuAuthenticationRequired,
                serviceSessionID,
                createConsentIfNone,
                null,
                null,
                HEADER_COMPUTE_PSU_IP_ADDRESS,
                null,
                fintechDecoupledPreferred,
                fintechBrandLoggingInformation,
                fintechNotificationURI,
                fintechNotificationContentPreferred,
                withBalance,
                online);
    }

    @SuppressWarnings("checkstyle:MethodLength") // Long method argument list written in column style for clarity
    private ResponseEntity consentNotYetAvailable(String bankProfileID, SessionEntity sessionEntity, String redirectCode, UUID xRequestId, Boolean psuAuthenticationRequired,
                                                  Optional<ConsentEntity> optionalConsent, boolean withBalance, Boolean online, String createConsentIfNone, Boolean fintechDecoupledPreferred,
                                                  String fintechBrandLoggingInformation, String fintechNotificationURI, String fintechNotificationContentPreferred) {
        log.info("do LOT (instead of loa) for bank {} {} consent", bankProfileID, optionalConsent.isPresent() ? "with" : "without");
        UUID serviceSessionID = optionalConsent.map(ConsentEntity::getTppServiceSessionId).orElse(null);
        var response = tppAisClient.getTransactionsWithoutAccountId(
                sessionEntity.getUserEntity().getFintechUserId(),
                RedirectUrlsEntity.buildOkUrl(uiConfig, redirectCode),
                RedirectUrlsEntity.buildNokUrl(uiConfig, redirectCode),
                xRequestId,
                COMPUTE_X_TIMESTAMP_UTC,
                COMPUTE_X_REQUEST_SIGNATURE,
                COMPUTE_FINTECH_ID,
                null,
                tppProperties.getFintechDataProtectionPassword(),
                UUID.fromString(bankProfileID),
                psuAuthenticationRequired,
                serviceSessionID,
                createConsentIfNone,
                null,
                null,
                HEADER_COMPUTE_PSU_IP_ADDRESS,
                null, null, null, null,
                null, null, null, null
        );
        if (response.getStatusCode() != HttpStatus.OK) {
            return response;
        }
        return consentAvailable(
                bankProfileID,
                sessionEntity,
                redirectCode,
                UUID.randomUUID(),
                optionalConsent,
                createConsentIfNone,
                withBalance,
                psuAuthenticationRequired,
                online,
                fintechDecoupledPreferred,
                fintechBrandLoggingInformation,
                fintechNotificationURI,
                fintechNotificationContentPreferred
        );
    }
}

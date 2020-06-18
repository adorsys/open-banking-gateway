package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.LoARetrievalInformation;
import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.AccountEntity;
import de.adorsys.opba.fintech.impl.database.entities.ConsentEntity;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.AccountRepository;
import de.adorsys.opba.fintech.impl.database.repositories.ConsentRepository;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import de.adorsys.opba.fintech.impl.tppclients.Actions;
import de.adorsys.opba.fintech.impl.tppclients.ConsentType;
import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountDetails;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountStatus;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private final AccountRepository accountRepository;
    private final ConsentService consentService;
    private final BankSearchService searchService;

    public ResponseEntity listAccounts(SessionEntity sessionEntity,
                                       String fintechOkUrl, String fintechNOKUrl,
                                       String bankId, LoARetrievalInformation loARetrievalInformation) {

        log.info("List of accounts {}", loARetrievalInformation);
        if (loARetrievalInformation.equals(LoARetrievalInformation.fromFintechCache)) {
            return createLoAResponseFromDatabase(sessionEntity.getUserEntity(), bankId);
        }

        final String fintechRedirectCode = UUID.randomUUID().toString();

        if (loARetrievalInformation.equals(LoARetrievalInformation.fromTppWithNewConsent)) {
            consentService.deleteConsent(sessionEntity.getUserEntity(), ConsentType.AIS, bankId);
        }
        ResponseEntity accounts = readOpbaResponse(bankId, sessionEntity, fintechRedirectCode);

        switch (accounts.getStatusCode()) {
            case OK:
                mergeKnownAccountsWithNewAccounts(sessionEntity.getUserEntity(), bankId, (AccountList) accounts.getBody());
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
    }

    private ResponseEntity createLoAResponseFromDatabase(UserEntity userEntity, String bankid) {
        List<AccountEntity> accountEntityList = new ArrayList<>();
        accountRepository.findByUserEntityAndBankId(userEntity, bankid).forEach(el -> accountEntityList.add(el));
        List<AccountDetails> accountDetailList = new ArrayList<>();
        for (AccountEntity accountEntity : accountEntityList) {
            AccountDetails accountDetails = new AccountDetails();
            accountDetails.setIban(accountEntity.getIban());
            accountDetails.setName(accountEntity.getName());
            accountDetails.setResourceId(accountEntity.getResourceId());
            accountDetails.setCurrency(accountEntity.getCurrency());
            accountDetails.setStatus(AccountStatus.fromValue(accountEntity.getStatus()));
            accountDetailList.add(accountDetails);
        }
        AccountList accountList = new AccountList();
        accountList.setAccounts(accountDetailList);
        log.info("found {} accounts for bankd {} for user {}", accountDetailList.size(), bankid, userEntity.getLoginUserName());
        return new ResponseEntity<>(accountList, HttpStatus.OK);
    }

    @Transactional
    void mergeKnownAccountsWithNewAccounts(UserEntity userEntity, String bankId, AccountList tppAccountList) {
        Map<String, AccountEntity> fintechAccountMap = new HashMap<>();
        accountRepository.findByUserEntityAndBankId(userEntity, bankId).forEach(el -> fintechAccountMap.put(el.getIban(), el));
        Set<String> fintechIbans = fintechAccountMap.keySet();

        Map<String, AccountDetails> tppAccountMap = new HashMap<>();
        tppAccountList.getAccounts().forEach(el -> tppAccountMap.put(el.getIban(), el));
        Set<String> tppIbans = tppAccountMap.keySet();

        Set<String> becameUnknownIbans = new HashSet<>(fintechIbans);
        becameUnknownIbans.removeAll(tppIbans);

        // these ibans were known before, now they are unknown
        for (String unknownIban : becameUnknownIbans) {
            fintechAccountMap.get(unknownIban).setBecameUnknown(true);
            accountRepository.save(fintechAccountMap.get(unknownIban));
        }
        Set<String> newIbans = new HashSet<>(tppIbans);
        newIbans.removeAll(fintechIbans);
        // these ibans are new and have to be persisted
        for (String newIban : newIbans) {
            AccountDetails tppAccountDetails = tppAccountMap.get(newIban);
            accountRepository.save(
                    AccountEntity.builder()
                            .name(tppAccountDetails.getName())
                            .resourceId(tppAccountDetails.getResourceId())
                            .bankId(bankId).iban(newIban)
                            .currency(tppAccountDetails.getCurrency())
                            // TODO Status is not given is listTransactions is done as long as 303 is not done
                            .status(tppAccountDetails.getStatus() != null ? tppAccountDetails.getStatus().toString() : null)
                            .userEntity(userEntity)
                            .becameUnknown(false)
                            .build()
            );
        }
        fintechIbans.stream().forEach(iban -> log.debug("before merge: fintechIban:{}", iban));
        tppIbans.stream().forEach(iban -> log.debug("before merge: tppIban:    {}", iban));
        becameUnknownIbans.stream().forEach(iban -> log.debug("after merge unknown fintech iban: {}", iban));
        newIbans.stream().forEach(iban -> log.debug("after merge new fintech iban:     {}", iban));
    }

    private ResponseEntity readOpbaResponse(String bankID, SessionEntity sessionEntity, String redirectCode) {
        UUID xRequestId = UUID.fromString(restRequestContext.getRequestId());
        Optional<ConsentEntity> optionalConsent = consentRepository.findByUserEntityAndBankIdAndConsentTypeAndConsentConfirmed(sessionEntity.getUserEntity(),
                bankID, ConsentType.AIS, Boolean.TRUE);
        if (optionalConsent.isPresent()) {
            log.info("LoA found valid ais consent for user {} bank {}", sessionEntity.getUserEntity().getLoginUserName(), bankID);
            return bankDoesNotRequireConsent(bankID, sessionEntity, redirectCode, xRequestId, optionalConsent);
        }

        BankProfileResponse bankProfile = searchService.getBankProfileById(bankID).getBody();
        if (null != bankProfile.getBankProfileDescriptor().getConsentSupportByService()
                && "true".equals(bankProfile.getBankProfileDescriptor().getConsentSupportByService().get(Actions.LIST_ACCOUNTS.name()))) {
            log.info("LoA no valid ais consent for user {} bank {} available", sessionEntity.getUserEntity().getLoginUserName(), bankID);
            // FIXME: HACKETTY-HACK - force consent retrieval for transactions on ALL accounts
            // Should be superseded and fixed with
            // https://github.com/adorsys/open-banking-gateway/issues/303
            return bankRequiresConsent(bankID, sessionEntity, redirectCode, xRequestId, optionalConsent);
        }

        return bankDoesNotRequireConsent(bankID, sessionEntity, redirectCode, xRequestId, optionalConsent);
    }

    private ResponseEntity bankDoesNotRequireConsent(String bankID, SessionEntity sessionEntity, String redirectCode, UUID xRequestId, Optional<ConsentEntity> optionalConsent) {
        log.info("do LOA for bank {} {} consent", bankID, optionalConsent.isPresent() ? "with" : "without");
        UUID serviceSessionID = optionalConsent.isPresent() ? optionalConsent.get().getTppServiceSessionId() : null;
        return tppAisClient.getAccounts(
                tppProperties.getServiceSessionPassword(),
                sessionEntity.getUserEntity().getFintechUserId(),
                RedirectUrlsEntity.buildOkUrl(uiConfig, redirectCode),
                RedirectUrlsEntity.buildNokUrl(uiConfig, redirectCode),
                xRequestId,
                COMPUTE_X_TIMESTAMP_UTC,
                OperationType.AIS.toString(),
                COMPUTE_X_REQUEST_SIGNATURE,
                COMPUTE_FINTECH_ID,
                bankID,
                null,
                serviceSessionID);
    }

    private ResponseEntity bankRequiresConsent(String bankID, SessionEntity sessionEntity, String redirectCode, UUID xRequestId, Optional<ConsentEntity> optionalConsent) {
        log.info("do LOT (instead of loa) for bank {} {} consent", bankID, optionalConsent.isPresent() ? "with" : "without");
        UUID serviceSessionID = optionalConsent.isPresent() ? optionalConsent.get().getTppServiceSessionId() : null;
        return tppAisClient.getTransactions(
                UUID.randomUUID().toString(), // As consent is missing this will be ignored
                tppProperties.getServiceSessionPassword(),
                sessionEntity.getUserEntity().getFintechUserId(),
                RedirectUrlsEntity.buildOkUrl(uiConfig, redirectCode),
                RedirectUrlsEntity.buildNokUrl(uiConfig, redirectCode),
                xRequestId,
                COMPUTE_X_TIMESTAMP_UTC,
                OperationType.AIS.toString(),
                COMPUTE_X_REQUEST_SIGNATURE,
                COMPUTE_FINTECH_ID,
                bankID, null, serviceSessionID, null, null, null, null, null);
    }

}

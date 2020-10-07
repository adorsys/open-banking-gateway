package de.adorsys.opba.fireflyexporter.service;

import de.adorsys.opba.fireflyexporter.client.TppAisClient;
import de.adorsys.opba.fireflyexporter.client.TppBankSearchClient;
import de.adorsys.opba.fireflyexporter.config.ApiConfig;
import de.adorsys.opba.fireflyexporter.config.OpenBankingConfig;
import de.adorsys.opba.fireflyexporter.entity.AccountExportJob;
import de.adorsys.opba.fireflyexporter.entity.BankConsent;
import de.adorsys.opba.fireflyexporter.repository.AccountExportJobRepository;
import de.adorsys.opba.fireflyexporter.repository.BankConsentRepository;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.http.HttpHeaders.LOCATION;

@Service
@RequiredArgsConstructor
public class AccountExportService {

    private final ApiConfig apiConfig;
    private final TppAisClient aisApi;
    private final OpenBankingConfig bankingConfig;
    private final BankConsentRepository consentRepository;
    private final ConsentService consentService;
    private final FireFlyAccountExporter exporter;
    private final AccountExportJobRepository exportJobRepository;
    private final TppBankSearchClient bankSearch;

    @Transactional
    @SuppressWarnings("CPD-START") // This is mostly example code how to use an application
    public ResponseEntity<Long> exportAccounts(String fireFlyToken, String bankId) {
        UUID redirectCode = UUID.randomUUID();
        UUID consentId = consentRepository.findFirstByBankIdOrderByModifiedAt(bankId).map(BankConsent::getConsentId).orElse(UUID.randomUUID());
        ResponseEntity<AccountList> accounts = aisApi.getAccounts(
                bankingConfig.getDataProtectionPassword(),
                bankingConfig.getUserId(),
                apiConfig.getRedirectOkUri(redirectCode.toString()),
                apiConfig.getRedirectNokUri(),
                UUID.randomUUID(),
                null,
                null,
                null,
                bankId,
                null,
                consentId,
                true
        );

        if (accounts.getStatusCode() == HttpStatus.ACCEPTED) {
            String redirectTo = accounts.getHeaders().get(LOCATION).get(0);
            // HBCI banks do not support consent, so asking for global consent makes no sense
            if (consentSupportedByBank(bankId)) {
                redirectTo = consentService.createConsentForAccountsAndTransactionsAndSaveSession(bankId);
            } else {
                consentService.saveSession(bankId, redirectCode, consentId, accounts);
            }
            return ResponseEntity.accepted().header(LOCATION, redirectTo).build();
        }

        AccountExportJob exportJob = exportJobRepository.save(new AccountExportJob());
        exporter.exportToFirefly(fireFlyToken, exportJob.getId(), accounts.getBody());
        return ResponseEntity.ok(exportJob.getId());
    }

    private boolean consentSupportedByBank(String bankId) {
        ResponseEntity<BankProfileResponse> bankProfile = bankSearch.bankProfileGET(UUID.randomUUID(), bankId, null, null, null);
        return "true".equals(bankProfile.getBody().getBankProfileDescriptor().getConsentSupportByService().get("LIST_ACCOUNTS"));
    }
}

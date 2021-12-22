package de.adorsys.opba.fireflyexporter.service;

import de.adorsys.opba.fireflyexporter.client.TppAisClient;
import de.adorsys.opba.fireflyexporter.config.ApiConfig;
import de.adorsys.opba.fireflyexporter.config.OpenBankingConfig;
import de.adorsys.opba.fireflyexporter.entity.AccountExportJob;
import de.adorsys.opba.fireflyexporter.entity.BankConsent;
import de.adorsys.opba.fireflyexporter.repository.AccountExportJobRepository;
import de.adorsys.opba.fireflyexporter.repository.BankConsentRepository;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
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

    @Transactional
    @SuppressWarnings("CPD-START") // This is mostly example code how to use an application
    public ResponseEntity<Long> exportAccounts(String fireFlyToken, UUID bankProfileId) {
        ResponseEntity<AccountList> accounts = aisApi.getAccounts(
                bankingConfig.getUserId(),
                apiConfig.getRedirectOkUri(UUID.randomUUID().toString()),
                apiConfig.getRedirectNokUri(),
                UUID.randomUUID(),
                null,
                null,
                null,
                null,
                bankingConfig.getDataProtectionPassword(),
                bankProfileId,
                null,
                consentRepository.findFirstByBankProfileUuidOrderByModifiedAtDesc(bankProfileId).map(BankConsent::getConsentId).orElse(null),
                "",
                null,
                null,
                true,
                null,
                true,
                null
        );

        if (accounts.getStatusCode() == HttpStatus.ACCEPTED) {
            String redirectTo = consentService.createConsentForAccountsAndTransactions(bankProfileId);
            return ResponseEntity.accepted().header(LOCATION, redirectTo).build();
        }

        AccountExportJob exportJob = exportJobRepository.save(new AccountExportJob());
        exporter.exportToFirefly(fireFlyToken, exportJob.getId(), accounts.getBody());
        return ResponseEntity.ok(exportJob.getId());
    }
}

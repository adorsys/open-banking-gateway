package de.adorsys.opba.fireflyexporter.service;

import de.adorsys.opba.fireflyexporter.config.ApiConfig;
import de.adorsys.opba.fireflyexporter.config.OpenBankingConfig;
import de.adorsys.opba.fireflyexporter.entity.AccountExportJob;
import de.adorsys.opba.fireflyexporter.entity.BankConsent;
import de.adorsys.opba.fireflyexporter.repository.AccountExportJobRepository;
import de.adorsys.opba.fireflyexporter.repository.BankConsentRepository;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import de.adorsys.opba.tpp.ais.api.resource.generated.TppBankingApiAccountInformationServiceAisApi;
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
    private final TppBankingApiAccountInformationServiceAisApi aisApi;
    private final OpenBankingConfig bankingConfig;
    private final BankConsentRepository consentRepository;
    private final ConsentCreationService consentCreationService;
    private final FireFlyExporter exporter;
    private final AccountExportJobRepository exportJobRepository;

    @Transactional
    public ResponseEntity<Long> exportAccounts(String fireFlyToken, String bankId) {
        ResponseEntity<AccountList> accounts = aisApi.getAccounts(
                bankingConfig.getDataProtectionPassword(),
                bankingConfig.getUserId(),
                apiConfig.getRedirectOk().toASCIIString(),
                apiConfig.getRedirectNok().toASCIIString(),
                UUID.randomUUID(),
                null,
                null,
                null,
                bankId,
                null,
                consentRepository.findBankConsentByBankId(bankId).map(BankConsent::getConsentId).orElse(null),
                true
        );

        if (accounts.getStatusCode() == HttpStatus.ACCEPTED) {
            String redirectTo = consentCreationService.createConsentForAccountsAndTransactions(bankId);
            return ResponseEntity.accepted().header(LOCATION, redirectTo).build();
        }

        AccountExportJob exportJob = exportJobRepository.save(new AccountExportJob());
        exporter.exportToFirefly(fireFlyToken, exportJob, accounts.getBody());
        return ResponseEntity.ok(exportJob.getId());
    }
}

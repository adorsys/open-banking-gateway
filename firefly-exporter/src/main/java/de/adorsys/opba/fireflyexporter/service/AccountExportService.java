package de.adorsys.opba.fireflyexporter.service;

import de.adorsys.opba.fireflyexporter.config.ApiConfig;
import de.adorsys.opba.fireflyexporter.config.OpenBankingConfig;
import de.adorsys.opba.tppbankingapi.ais.resource.generated.TppBankingApiAccountInformationServiceAisApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountExportService {

    private final ApiConfig apiConfig;
    private final TppBankingApiAccountInformationServiceAisApi aisApi;
    private final OpenBankingConfig bankingConfig;

    @Transactional
    public void exportAccounts(String fireFlyToken, String bankId) {
        aisApi.getAccounts(
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
                null,
                true
        );
    }
}

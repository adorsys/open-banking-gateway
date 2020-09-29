package de.adorsys.opba.fireflyexporter.service;

import de.adorsys.opba.fireflyexporter.config.ApiConfig;
import de.adorsys.opba.fireflyexporter.config.OpenBankingConfig;
import de.adorsys.opba.fireflyexporter.repository.BankConsentRepository;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import de.adorsys.opba.tpp.ais.api.resource.generated.TppBankingApiAccountInformationServiceAisApi;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsentCreationService {

    private final ApiConfig apiConfig;
    private final TppBankingApiAccountInformationServiceAisApi aisApi;
    private final OpenBankingConfig bankingConfig;

    @Transactional
    public String createConsentForAccountsAndTransactions(String bankId) {
        ResponseEntity<TransactionsResponse> apiResponse = aisApi.getTransactionsWithoutAccountId(
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
                UUID.randomUUID(),
                null,
                null,
                null,
                "both",
                false
        );

        if (apiResponse.getStatusCode() == HttpStatus.ACCEPTED) {
            return apiResponse.getHeaders().get(HttpHeaders.LOCATION).get(0);
        }

        throw new IllegalStateException("Bad API return code: " + apiResponse.getStatusCode());
    }
}

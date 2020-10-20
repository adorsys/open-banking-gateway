package de.adorsys.opba.fireflyexporter.service;

import de.adorsys.opba.firefly.api.model.generated.AccountArray;
import de.adorsys.opba.fireflyexporter.client.FireflyAccountsApiClient;
import de.adorsys.opba.fireflyexporter.client.TppAisClient;
import de.adorsys.opba.fireflyexporter.config.ApiConfig;
import de.adorsys.opba.fireflyexporter.config.OpenBankingConfig;
import de.adorsys.opba.fireflyexporter.dto.ExportableAccount;
import de.adorsys.opba.fireflyexporter.entity.BankConsent;
import de.adorsys.opba.fireflyexporter.repository.BankConsentRepository;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.LOCATION;

@Service
@RequiredArgsConstructor
public class ExportableAccountService {

    private final ApiConfig apiConfig;
    private final TppAisClient aisApi;
    private final OpenBankingConfig bankingConfig;
    private final BankConsentRepository consentRepository;
    private final ConsentService consentService;
    private final FireflyAccountsApiClient accountsApi;
    private final FireFlyTokenProvider tokenProvider;

    @Transactional
    @SuppressWarnings("CPD-START") // This is mostly example code how to use an application
    public ResponseEntity<List<ExportableAccount>> exportableAccounts(String fireFlyToken, String bankId) {
        ResponseEntity<AccountList> accounts = aisApi.getAccounts(
                bankingConfig.getDataProtectionPassword(),
                bankingConfig.getUserId(),
                apiConfig.getRedirectOkUri(UUID.randomUUID().toString()),
                apiConfig.getRedirectNokUri(),
                UUID.randomUUID(),
                null,
                null,
                null,
                bankId,
                null,
                consentRepository.findFirstByBankIdOrderByModifiedAt(bankId).map(BankConsent::getConsentId).orElse(null),
                true,
                null
        );

        if (accounts.getStatusCode() == HttpStatus.ACCEPTED) {
            String redirectTo = consentService.createConsentForAccountsAndTransactions(bankId);
            return ResponseEntity.accepted().header(LOCATION, redirectTo).build();
        }
        if (accounts.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(accounts.getStatusCode()).build();
        }
        tokenProvider.setToken(fireFlyToken);
        ResponseEntity<AccountArray> fireflyAccounts = accountsApi.listAccount(null, null, null);
        if (fireflyAccounts.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(fireflyAccounts.getStatusCode()).build();
        }
        Set<String> fireflyIbans = fireflyAccounts.getBody().getData().stream()
                .map(it -> it.getAttributes().getIban())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<ExportableAccount> result = accounts.getBody().getAccounts().stream()
                .filter(it -> fireflyIbans.contains(it.getIban()))
                .map(it -> new ExportableAccount(it.getIban(), it.getResourceId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}

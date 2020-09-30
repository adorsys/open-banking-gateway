package de.adorsys.opba.fireflyexporter.service;

import de.adorsys.opba.fireflyexporter.client.TppConsentConfirmationClient;
import de.adorsys.opba.fireflyexporter.config.ApiConfig;
import de.adorsys.opba.fireflyexporter.config.OpenBankingConfig;
import de.adorsys.opba.fireflyexporter.entity.BankConsent;
import de.adorsys.opba.fireflyexporter.entity.RedirectState;
import de.adorsys.opba.fireflyexporter.repository.BankConsentRepository;
import de.adorsys.opba.fireflyexporter.repository.RedirectStateRepository;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import de.adorsys.opba.tpp.ais.api.resource.generated.TppBankingApiAccountInformationServiceAisApi;
import de.adorsys.opba.tpp.token.api.model.generated.PsuConsentSessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ApiConfig apiConfig;
    private final TppConsentConfirmationClient tppConsentClient;
    private final BankConsentRepository bankConsentRepository;
    private final TppBankingApiAccountInformationServiceAisApi aisApi;
    private final RedirectStateRepository redirectStateRepository;
    private final OpenBankingConfig bankingConfig;

    @Transactional
    public String createConsentForAccountsAndTransactions(String bankId) {
        UUID redirectCode = UUID.randomUUID();
        UUID serviceSessionId = UUID.randomUUID();

        ResponseEntity<TransactionsResponse> apiResponse = aisApi.getTransactionsWithoutAccountId(
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
                serviceSessionId,
                null,
                null,
                null,
                "both",
                false
        );

        if (apiResponse.getStatusCode() == HttpStatus.ACCEPTED) {
            RedirectState redirectState = new RedirectState();
            redirectState.setId(redirectCode);
            redirectState.setServiceSessionId(serviceSessionId);
            redirectState.setBankId(bankId);
            redirectState.setAuthorizationSessionId(apiResponse.getHeaders().get("Authorization-Session-ID").get(0));
            redirectStateRepository.save(redirectState);

            return apiResponse.getHeaders().get(HttpHeaders.LOCATION).get(0);
        }

        throw new IllegalStateException("Bad API return code: " + apiResponse.getStatusCode());
    }

    @Transactional
    public String confirmConsentAndGetBankId(String redirectCode) {
        RedirectState state = redirectStateRepository.findById(UUID.fromString(redirectCode))
                .orElseThrow(() -> new IllegalStateException("No redirect state for code: " + redirectCode));

        ResponseEntity<PsuConsentSessionResponse> response = tppConsentClient.confirmConsent(
                state.getAuthorizationSessionId(),
                UUID.randomUUID(),
                bankingConfig.getDataProtectionPassword(),
                null,
                null,
                null
        );

        if (HttpStatus.OK != response.getStatusCode()) {
            return null;
        }

        BankConsent consent = new BankConsent();
        consent.setBankId(state.getBankId());
        consent.setConsentId(state.getServiceSessionId());
        bankConsentRepository.save(consent);

        return consent.getBankId();
    }
}

package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.InlineResponseBankInfo;
import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
import de.adorsys.opba.fintech.impl.mapper.BankInfoMapper;
import de.adorsys.opba.fintech.impl.tppclients.TppIbanSearchClient;
import de.adorsys.opba.tpp.bankinfo.api.model.generated.BankInfoResponse;
import de.adorsys.opba.tpp.bankinfo.api.model.generated.SearchBankinfoBody;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_FINTECH_ID;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_REQUEST_SIGNATURE;

@Service
@Slf4j
@RequiredArgsConstructor
public class IbanSearchService {

    private final TppIbanSearchClient tppIbanSearchClient;
    private final RestRequestContext restRequestContext;
    private final BankInfoMapper bankInfoMapper;

    @SneakyThrows
    public InlineResponseBankInfo searchByIban(String iban) {

        SearchBankinfoBody body = new SearchBankinfoBody();
        body.setIban(iban);

        // Get the full response first
        try {
            ResponseEntity<BankInfoResponse> fullResponse = tppIbanSearchClient.getBankInfoByIban(UUID.fromString(restRequestContext.getRequestId()),
                    body,
                    COMPUTE_FINTECH_ID,
                    COMPUTE_X_REQUEST_SIGNATURE);

            BankInfoResponse response = fullResponse.getBody();
            if (response == null) {
                log.error("Received null response from TPP client for IBAN: {}", iban);
                return null;
            }

            return bankInfoMapper.mapFromTppToFintech(response);
        } catch (Exception e) {
            log.error("Error calling TPP Service: ", e);
            throw e;
        }
    }
}

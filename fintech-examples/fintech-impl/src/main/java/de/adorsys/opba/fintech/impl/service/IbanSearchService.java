package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.InlineResponseBankInfo;
import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
import de.adorsys.opba.fintech.impl.mapper.BankInfoMapper;
import de.adorsys.opba.fintech.impl.service.exceptions.InvalidIbanException;
import de.adorsys.opba.fintech.impl.tppclients.TppIbanSearchClient;
import de.adorsys.opba.tpp.bankinfo.api.model.generated.BankInfoResponse;
import de.adorsys.opba.tpp.bankinfo.api.model.generated.SearchBankinfoBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;
import org.iban4j.IbanFormatException;
import org.iban4j.IbanUtil;
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

    public InlineResponseBankInfo searchByIban(String iban) {
        if (iban == null || iban.trim().isEmpty()) {
            throw new InvalidIbanException("No IBAN was provided. Please enter a valid IBAN.");
        }

        try {
            IbanUtil.validate(iban); // Validates format and checksum
        } catch (IbanFormatException | IllegalArgumentException e) {
            throw new InvalidIbanException("The IBAN you provided is invalid. Please check and try again.");
        }

        SearchBankinfoBody body = new SearchBankinfoBody();
        body.setIban(iban);

        try {
            ResponseEntity<BankInfoResponse> fullResponse = tppIbanSearchClient.getBankInfoByIban(
                    UUID.fromString(restRequestContext.getRequestId()),
                    body,
                    COMPUTE_FINTECH_ID,
                    COMPUTE_X_REQUEST_SIGNATURE
            );

            BankInfoResponse response = fullResponse.getBody();
            if (response == null) {
                throw new InvalidIbanException("Unable to find bank info for the provided IBAN.");
            }

            return bankInfoMapper.mapFromTppToFintech(response);
        } catch (Exception e) {
            throw new InvalidIbanException("An unexpected error occurred while processing your request. Please try again later.");
        }
    }
}

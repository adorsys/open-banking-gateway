package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.tppbankingapi.bankinfo.model.generated.BankInfoResponse;
import de.adorsys.opba.tppbankingapi.bankinfo.model.generated.SearchBankinfoBody;
import de.adorsys.opba.tppbankingapi.bankinfo.resource.generated.TppBankInfoApi;
import de.adorsys.opba.tppbankingapi.service.BankInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TppBankInfoController implements TppBankInfoApi {

    private final BankInfoService bankInfoService;

    @Override
    public ResponseEntity<BankInfoResponse> getBankInfoByIban(UUID xRequestID, SearchBankinfoBody body, String fintechID, String xRequestSignature) {
        log.info("Received IBAN lookup request: {}", body.getIban());

        var response = bankInfoService.getBankInfoByIban(body.getIban());

        if (response == null) {
            log.warn("No bank info found for IBAN: {}", body.getIban());
            return ResponseEntity.notFound().build();
        }

        log.info("Returning bank info: {}", response);
        return ResponseEntity.ok(response);
    }
}

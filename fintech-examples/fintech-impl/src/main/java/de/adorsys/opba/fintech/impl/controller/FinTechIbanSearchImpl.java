package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.InlineResponseBankInfo;
import de.adorsys.opba.fintech.api.model.generated.SearchBankInfoBody;
import de.adorsys.opba.fintech.api.resource.generated.FinTechIbanSearchApi;
import de.adorsys.opba.fintech.impl.service.IbanSearchService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FinTechIbanSearchImpl implements FinTechIbanSearchApi {

    private final IbanSearchService ibanSearchService;

    @Override
    public ResponseEntity<InlineResponseBankInfo> getBankInfoByIban(
            UUID xRequestID,
            String xXsrfToken,
            SearchBankInfoBody body
    ) {

        InlineResponseBankInfo fintechModel = ibanSearchService.searchByIban(
                body.getIban()
        );

        return new ResponseEntity<>(fintechModel, HttpStatus.OK);
    }
}

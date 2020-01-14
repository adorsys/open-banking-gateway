package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.InlineResponse2001;
import de.adorsys.opba.fintech.api.resource.FinTechBankSearchApi;
import de.adorsys.opba.fintech.impl.service.BankSearchService;
import de.adorsys.opba.fintech.impl.service.FinTechTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
public class FinTechBankSearchImpl implements FinTechBankSearchApi {


    @Autowired
    BankSearchService bankSearchService;

    @Autowired
    FinTechTokenService finTechTokenService;

    @Override
    public ResponseEntity<InlineResponse2001> bankSearchGET(UUID xRequestID, String fintechToken, String keyword, Integer start, Integer max) {
        log.info("search bank");
        if (!finTechTokenService.validate(fintechToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(bankSearchService.searchBank(keyword, start, max), HttpStatus.OK);
    }
}

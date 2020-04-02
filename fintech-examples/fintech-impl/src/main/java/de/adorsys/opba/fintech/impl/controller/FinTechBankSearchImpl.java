package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse2001;
import de.adorsys.opba.fintech.api.model.generated.InlineResponse2002;
import de.adorsys.opba.fintech.api.resource.generated.FinTechBankSearchApi;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.service.BankSearchService;
import de.adorsys.opba.fintech.impl.service.ContextInformation;
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
    private BankSearchService bankSearchService;

    @Autowired
    private AuthorizeService authorizeService;

    @Override
    public ResponseEntity<InlineResponse2001> bankSearchGET(UUID xRequestID, String fintechToken, String keyword, Integer start, Integer max) {
        if (!authorizeService.isAuthorized()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ContextInformation contextInformation = new ContextInformation();
        return new ResponseEntity<>(bankSearchService.searchBank(contextInformation, keyword, start, max), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<InlineResponse2002> bankProfileGET(UUID xRequestID, String fintechToken, String bankId) {
        if (!authorizeService.isAuthorized()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ContextInformation contextInformation = new ContextInformation();
        return new ResponseEntity<>(bankSearchService.searchBankProfile(contextInformation, bankId), HttpStatus.OK);
    }
}

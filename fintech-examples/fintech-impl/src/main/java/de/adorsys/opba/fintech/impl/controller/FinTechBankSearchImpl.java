package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.model.InlineResponse2001;
import de.adorsys.opba.fintech.api.resource.FinTechBankSearchApi;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.service.BankSearchService;
import de.adorsys.opba.fintech.impl.service.FinTechTokenService;
import de.adorsys.opba.fintech.impl.service.entities.ContextInformation;
import de.adorsys.opba.tpp.bankserach.api.resource.TppBankSearchApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
public class FinTechBankSearchImpl implements FinTechBankSearchApi {

    @Value("${tpp.url}")
    String tppUrl;

    @Autowired
    BankSearchService bankSearchService;

    @Autowired
    FinTechTokenService finTechTokenService;

    @Autowired
    AuthorizeService authorizeService;
    @Override
    public ResponseEntity<InlineResponse2001> bankSearchGET(UUID xRequestID, String fintechToken, String keyword, Integer start, Integer max) {
        if (!authorizeService.isAuthorized(fintechToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        TppBankSearchApi tppBankSearchApi = new TppBankSearchApi();
        tppBankSearchApi.getApiClient().setBasePath(tppUrl);

        ContextInformation contextInformation = new ContextInformation(xRequestID);
        return new ResponseEntity<>(bankSearchService.searchBank(tppBankSearchApi, contextInformation, keyword, start, max), HttpStatus.OK);
    }
}

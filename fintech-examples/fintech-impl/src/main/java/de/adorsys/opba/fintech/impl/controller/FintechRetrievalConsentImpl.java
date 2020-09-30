package de.adorsys.opba.fintech.impl.controller;

import de.adorsys.opba.fintech.api.resource.generated.FintechRetrieveConsentApi;
import de.adorsys.opba.fintech.impl.service.ConsentRetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Profile("CONSENT_RETRIEVAL")
public class FintechRetrievalConsentImpl implements FintechRetrieveConsentApi {
    private final ConsentRetrievalService consentRetrievalService;

    public ResponseEntity<Object> retrieveConsent(String userid, String password) {
        ConsentRetrievalService.ConsentRetrievalResult consentRetrievalResult = consentRetrievalService.get(userid, password);
        log.info("found:{}", consentRetrievalResult.toString());
        return new ResponseEntity<>(consentRetrievalResult, HttpStatus.OK);
    }
}

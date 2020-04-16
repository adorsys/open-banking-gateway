package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.properties.TppProperties;
import de.adorsys.opba.fintech.impl.tppclients.TppConsenClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsentService {

    private final TppConsenClient tppConsenClient;
    private final TppProperties tppProperties;

    public boolean confirmConsent(String authId, UUID xRequestId) {
        HttpStatus statusCode = tppConsenClient.confirmConsent(
                authId,
                xRequestId,
                tppProperties.getServiceSessionPassword()
        ).getStatusCode();
        log.debug("consent confirmation response code: {}", statusCode);
        return statusCode.is2xxSuccessful();
    }
}

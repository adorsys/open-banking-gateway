package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.tppclients.TppConsenClient;
import de.adorsys.opba.tpp.token.api.model.generated.PsuConsentSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsentService {
    private final TppConsenClient tppConsenClient;

    public PsuConsentSessionResponse confirmConsent(String authId, UUID xRequestId, String redirectCode) {
        return tppConsenClient.confirmConsent(authId, xRequestId, redirectCode).getBody();
    }
}

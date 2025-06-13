package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@Component
public class Xs2aAccountListConsentServiceProvider {

    private final Set<Xs2aAccountListConsentService> xs2aAccountListConsentServices;

    public Xs2aAccountListConsentService instance(Xs2aAisContext context) {
        return xs2aAccountListConsentServices.stream()
                .filter(it -> it.isXs2aApiVersionSupported(context.aspspProfile().getSupportedXs2aApiVersion()))
                .findAny().orElseThrow();
    }
}

package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import com.google.common.base.Strings;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service to find already existing consent based on the context.
 */
@Service("xs2aConsentFinder")
@RequiredArgsConstructor
public class ConsentFinder {

    public boolean consentExists(Xs2aContext context) {
        return context.isConsentAcquired() || !Strings.isNullOrEmpty(context.getConsentId());
    }
}

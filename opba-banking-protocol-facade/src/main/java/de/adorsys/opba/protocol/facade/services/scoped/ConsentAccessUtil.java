package de.adorsys.opba.protocol.facade.services.scoped;

import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

@UtilityClass
public class ConsentAccessUtil {

    @NotNull
    public Optional<ProtocolFacingConsent> getProtocolFacingConsent(Collection<ProtocolFacingConsent> consents) {
        if (consents.isEmpty()) {
            return Optional.empty();
        }

        if (consents.size() > 1) {
            throw new IllegalStateException("Too many consents");
        }

        return Optional.of(consents.iterator().next());
    }
}

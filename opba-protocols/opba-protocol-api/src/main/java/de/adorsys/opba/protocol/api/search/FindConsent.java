package de.adorsys.opba.protocol.api.search;

import de.adorsys.opba.protocol.api.dto.consent.ConsentResult;
import de.adorsys.opba.protocol.api.dto.consent.ConsentSpec;

import java.util.Set;
import java.util.function.BiPredicate;

// TODO: Hide it - protocol should deal with consent search itself.
@FunctionalInterface
public interface FindConsent extends BiPredicate<ConsentSpec, Set<ConsentResult>> {
}

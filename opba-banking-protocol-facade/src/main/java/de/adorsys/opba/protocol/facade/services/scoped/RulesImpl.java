package de.adorsys.opba.protocol.facade.services.scoped;

import de.adorsys.opba.db.domain.entity.IgnoreValidationRule;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.services.scoped.validation.Rules;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static de.adorsys.opba.protocol.api.common.Approach.EMBEDDED;
import static de.adorsys.opba.protocol.api.common.Approach.REDIRECT;

@RequiredArgsConstructor
public class RulesImpl<T> implements Rules {
    private final List<IgnoreValidationRule> validationRules;
    private final Class<T> invokerClass;
    private final Approach approach;

    @Override
    public boolean apply() {
        return validationRules.stream()
                .filter(it -> null == it.getEndpointClassCanonicalName()
                        || it.getEndpointClassCanonicalName().equals(invokerClass.getCanonicalName()))
                .filter(it -> !(EMBEDDED.equals(approach) && it.isForEmbedded()))
                .anyMatch(it -> !(REDIRECT.equals(approach) && it.isForRedirect()));
    }
}

package de.adorsys.opba.protocol.facade.services.scoped;

import de.adorsys.opba.db.domain.entity.IgnoreBankValidationRule;
import de.adorsys.opba.db.repository.jpa.IgnoreBankValidationRuleRepository;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.api.services.scoped.validation.IgnoreFieldsLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.adorsys.opba.protocol.api.common.Approach.EMBEDDED;
import static de.adorsys.opba.protocol.api.common.Approach.REDIRECT;

@Getter
@Setter
@RequiredArgsConstructor
public class IgnoreFieldsLoaderImpl implements IgnoreFieldsLoader {

    private Long protocolId;

    private final IgnoreBankValidationRuleRepository ignoreBankValidationRuleRepository;

    @Override
    public boolean apply(FieldCode fieldCode, Class invokerClass, Approach approach) {
        List<IgnoreBankValidationRule> validationRules = ignoreBankValidationRuleRepository.findByProtocolId(protocolId);
        Set<FieldCode> fieldsToIgnore = validationRules.stream()
                .filter(it -> it.getEndpointClassCanonicalName().equals(invokerClass.getCanonicalName()))
                .filter(it -> !EMBEDDED.equals(approach) || it.isForEmbedded())
                .filter(it -> !REDIRECT.equals(approach) || it.isForRedirect())
                .map(IgnoreBankValidationRule::getValidationCode)
                .collect(Collectors.toSet());
        return fieldsToIgnore.contains(fieldCode);
    }
}

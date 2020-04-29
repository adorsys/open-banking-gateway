package de.adorsys.opba.protocol.facade.services.scoped;

import de.adorsys.opba.db.domain.entity.IgnoreValidationRule;
import de.adorsys.opba.db.repository.jpa.IgnoreBankValidationRuleRepository;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.api.services.scoped.validation.IgnoreFieldsLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.adorsys.opba.protocol.api.common.Approach.EMBEDDED;
import static de.adorsys.opba.protocol.api.common.Approach.REDIRECT;

@Getter
@Setter
@RequiredArgsConstructor
@Service
public class IgnoreFieldsLoaderImpl implements IgnoreFieldsLoader {

    private Long protocolId;

    private final IgnoreBankValidationRuleRepository ignoreBankValidationRuleRepository;

    @Override
    public boolean apply(FieldCode fieldCode, Class invokerClass, Approach approach) {
        List<IgnoreValidationRule> validationRules = ignoreBankValidationRuleRepository.findByProtocolId(protocolId);
        Set<FieldCode> fieldsToIgnore = validationRules.stream()
                .filter(it -> null == it.getEndpointClassCanonicalName() || it.getEndpointClassCanonicalName().equals(invokerClass.getCanonicalName()))
                .filter(it -> !(EMBEDDED.equals(approach) && it.isForEmbedded()))
                .filter(it -> !(REDIRECT.equals(approach) && it.isForRedirect()))
                .map(IgnoreValidationRule::getValidationCode)
                .collect(Collectors.toSet());
        return !fieldsToIgnore.contains(fieldCode);
    }
}

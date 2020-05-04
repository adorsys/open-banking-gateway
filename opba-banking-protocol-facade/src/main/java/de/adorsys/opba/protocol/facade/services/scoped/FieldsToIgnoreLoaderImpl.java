package de.adorsys.opba.protocol.facade.services.scoped;

import de.adorsys.opba.db.repository.jpa.IgnoreValidationRuleRepository;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.api.services.scoped.validation.FieldsToIgnoreLoader;
import de.adorsys.opba.protocol.api.services.scoped.validation.IgnoreValidationRule;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class FieldsToIgnoreLoaderImpl implements FieldsToIgnoreLoader {

    private final Long protocolId;
    private final IgnoreValidationRuleRepository ignoreValidationRuleRepository;

    @Override
    public <T> Map<FieldCode, IgnoreValidationRule> getIgnoreValidationRules(Class<T> invokerClass, Approach approach) {
        Map<FieldCode, List<de.adorsys.opba.db.domain.entity.IgnoreValidationRule>> ruleByCode = ignoreValidationRuleRepository
                .findByProtocolId(protocolId).stream()
                .collect(Collectors.groupingBy(de.adorsys.opba.db.domain.entity.IgnoreValidationRule::getValidationCode));

        return ruleByCode.entrySet().stream().collect(toMap(
                Map.Entry::getKey,
                it -> new IgnoreValidationRuleImpl<>(it.getValue(), invokerClass, approach)
        ));
    }
}

package de.adorsys.opba.protocol.facade.services.scoped;

import de.adorsys.opba.db.domain.entity.IgnoreValidationRule;
import de.adorsys.opba.db.repository.jpa.IgnoreBankValidationRuleRepository;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.api.services.scoped.validation.IgnoreFieldsLoader;
import de.adorsys.opba.protocol.api.services.scoped.validation.Rules;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class IgnoreFieldsLoaderImpl implements IgnoreFieldsLoader {

    private final Long protocolId;
    private final IgnoreBankValidationRuleRepository ignoreBankValidationRuleRepository;

    @Override
    public <T> Map<FieldCode, Rules> getValidationRules(Class<T> invokerClass, Approach approach) {
        Map<FieldCode, List<IgnoreValidationRule>> codeListMap = ignoreBankValidationRuleRepository
                .findByProtocolId(protocolId).stream()
                .collect(Collectors.groupingBy(IgnoreValidationRule::getValidationCode));

        return codeListMap.entrySet().stream().collect(toMap(
                Map.Entry::getKey,
                it -> new RulesImpl<>(it.getValue(), invokerClass, approach)
        ));
    }
}

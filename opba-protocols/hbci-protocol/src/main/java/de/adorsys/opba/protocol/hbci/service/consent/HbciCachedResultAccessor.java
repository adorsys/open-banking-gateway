package de.adorsys.opba.protocol.hbci.service.consent;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableObjectMapper;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableProperties;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.HbciResultCache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HbciCachedResultAccessor {

    private final FlowableProperties properties;
    private final FlowableObjectMapper mapper;

    @SneakyThrows
    @Transactional
    public Optional<HbciResultCache> resultFromCache(HbciContext context) {
        if (context.isConsentIncompatible()) {
            return Optional.empty();
        }

        Collection<HbciResultCache> consents = context.consentAccess().findByCurrentServiceSession()
                .stream()
                .map(this::readCachedEntry)
                .collect(Collectors.toList());

        if (consents.isEmpty()) {
            return Optional.empty();
        }

        HbciResultCache result = new HbciResultCache();
        for (HbciResultCache consent : consents) {
            if (null == result.getAccounts() || (null != consent.getAccounts() && consent.getAccounts().getCachedAt().isAfter(result.getAccounts().getCachedAt()))) {
                result.setAccounts(consent.getAccounts());
            }

            if (null != consent.getTransactionsByIban()) {
                if (null == result.getTransactionsByIban()) {
                    result.setTransactionsByIban(new HashMap<>());
                }

                mergeTransactions(result, consent);
            }
        }
        return Optional.of(result);
    }

    private void mergeTransactions(HbciResultCache result, HbciResultCache consent) {
        consent.getTransactionsByIban().forEach((iban, txn) -> result.getTransactionsByIban().compute(iban, (id, current) -> {
            if (null == current) {
                return txn;
            }
            if (txn.getCachedAt().isAfter(current.getCachedAt())) {
                return txn;
            }
            return current;
        }));
    }

    @SneakyThrows
    @Transactional
    public void resultToCache(HbciContext context, HbciResultCache result) {
        ProtocolFacingConsent newConsent = context.getRequestScoped().consentAccess().createDoNotPersist();
        newConsent.setConsentId(context.getSagaId());

        // Support for versioning using class name
        String className = result.getClass().getCanonicalName();
        if (!properties.canSerialize(className)) {
            throw new IllegalArgumentException("Class deserialization not allowed " + className);
        }

        newConsent.setConsentContext(mapper.writeValueAsString(ImmutableMap.of(className, result)));
        context.getRequestScoped().consentAccess().save(newConsent);
    }

    @SneakyThrows
    private HbciResultCache readCachedEntry(ProtocolFacingConsent target) {
        // Support for versioning using class name
        JsonNode value = mapper.readTree(target.getConsentContext());
        Map.Entry<String, JsonNode> classNameAndValue = value.fields().next();
        if (!properties.canSerialize(classNameAndValue.getKey())) {
            throw new IllegalArgumentException("Class deserialization not allowed " + classNameAndValue.getKey());
        }

        HbciResultCache cachedResult = (HbciResultCache) mapper.getMapper().readValue(
                classNameAndValue.getValue().traverse(),
                Class.forName(classNameAndValue.getKey())
        );

        return cachedResult;
    }
}

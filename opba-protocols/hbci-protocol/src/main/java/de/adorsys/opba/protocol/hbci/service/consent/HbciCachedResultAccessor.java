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

import java.util.Map;
import java.util.Optional;

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

        Optional<ProtocolFacingConsent> consent = context.consentAccess().findByCurrentServiceSession();

        if (!consent.isPresent() || null == consent.get().getConsentContext()) {
            return Optional.empty();
        }

        ProtocolFacingConsent target = consent.get();

        JsonNode value = mapper.readTree(target.getConsentContext());
        Map.Entry<String, JsonNode> classNameAndValue = value.fields().next();

        if (!properties.canSerialize(classNameAndValue.getKey())) {
            throw new IllegalArgumentException("Class deserialization not allowed " + classNameAndValue.getKey());
        }

        HbciResultCache cachedResult = (HbciResultCache) mapper.getMapper().readValue(
                classNameAndValue.getValue().traverse(),
                Class.forName(classNameAndValue.getKey())
        );

        return Optional.of(cachedResult);
    }

    @SneakyThrows
    @Transactional
    public void resultToCache(HbciContext context, HbciResultCache result) {
        ProtocolFacingConsent consent = context.consentAccess().findByCurrentServiceSession()
                .orElseGet(() -> {
                    ProtocolFacingConsent newConsent = context.getRequestScoped().consentAccess().createDoNotPersist();
                    newConsent.setConsentId(context.getSagaId());
                    return newConsent;
                });

        String className = result.getClass().getCanonicalName();
        if (!properties.canSerialize(className)) {
            throw new IllegalArgumentException("Class deserialization not allowed " + className);
        }

        consent.setConsentContext(mapper.writeValueAsString(ImmutableMap.of(className, result)));
        context.getRequestScoped().consentAccess().save(consent);
    }
}

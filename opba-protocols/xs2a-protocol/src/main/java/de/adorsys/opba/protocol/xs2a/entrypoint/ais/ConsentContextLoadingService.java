package de.adorsys.opba.protocol.xs2a.entrypoint.ais;

import com.fasterxml.jackson.databind.JsonNode;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableObjectMapper;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableProperties;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConsentContextLoadingService {

    private final FlowableProperties properties;
    private final FlowableObjectMapper mapper;

    @NotNull
    public Xs2aAisContext contextFromConsent(Optional<ProtocolFacingConsent> consent) {
        ProtocolFacingConsent target = consent.get();
        return contextFromConsent(target);
    }

    @NotNull
    @SneakyThrows
    public Xs2aAisContext contextFromConsent(ProtocolFacingConsent target) {
        JsonNode value = mapper.readTree(target.getConsentContext());
        Map.Entry<String, JsonNode> classNameAndValue = value.fields().next();

        if (!properties.getSerialization().canSerialize(classNameAndValue.getKey())) {
            throw new IllegalArgumentException("Class deserialization not allowed " + classNameAndValue.getKey());
        }

        Xs2aAisContext ctx = (Xs2aAisContext) mapper.getMapper().readValue(
                classNameAndValue.getValue().traverse(),
                Class.forName(classNameAndValue.getKey())
        );
        ctx.setConsentId(target.getConsentId());
        return ctx;
    }
}

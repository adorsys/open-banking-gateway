package de.adorsys.opba.protocol.xs2a.entrypoint.pis;

import com.fasterxml.jackson.databind.JsonNode;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingPayment;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableObjectMapper;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableProperties;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class Xs2aPaymentContextLoader {

    private final FlowableProperties properties;
    private final FlowableObjectMapper mapper;

    @SneakyThrows
    public Xs2aPisContext loadContext(ProtocolFacingPayment payment) {
        JsonNode value = mapper.readTree(payment.getPaymentContext());
        Map.Entry<String, JsonNode> classNameAndValue = value.fields().next();

        if (!properties.getSerialization().canSerialize(classNameAndValue.getKey())) {
            throw new IllegalArgumentException("Class deserialization not allowed " + classNameAndValue.getKey());
        }

        return (Xs2aPisContext) mapper.getMapper().readValue(
                classNameAndValue.getValue().traverse(),
                Class.forName(classNameAndValue.getKey())
        );
    }
}

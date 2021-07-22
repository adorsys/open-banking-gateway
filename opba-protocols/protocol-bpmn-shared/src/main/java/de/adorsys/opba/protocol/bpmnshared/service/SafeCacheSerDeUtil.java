package de.adorsys.opba.protocol.bpmnshared.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableObjectMapper;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SafeCacheSerDeUtil {

    private final FlowableProperties properties;
    private final FlowableObjectMapper mapper;

    public String safeSerialize(Object context) throws JsonProcessingException {
        // Support for versioning using class name
        String className = context.getClass().getCanonicalName();
        if (!properties.getSerialization().canSerialize(className)) {
            throw new IllegalArgumentException("Class deserialization not allowed " + className);
        }

        return mapper.writeValueAsString(ImmutableMap.of(className, context));
    }

    @SneakyThrows
    public Object safeDeserialize(String context) {
        // Support for versioning using class name
        JsonNode value = mapper.readTree(context);
        Map.Entry<String, JsonNode> classNameAndValue = value.fields().next();
        if (!properties.getSerialization().canSerialize(classNameAndValue.getKey())) {
            throw new IllegalArgumentException("Class deserialization not allowed " + classNameAndValue.getKey());
        }

        return mapper.getMapper().readValue(
                classNameAndValue.getValue().traverse(),
                Class.forName(classNameAndValue.getKey())
        );
    }
}

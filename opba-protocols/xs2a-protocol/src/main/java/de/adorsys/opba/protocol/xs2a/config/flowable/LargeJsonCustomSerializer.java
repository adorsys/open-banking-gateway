package de.adorsys.opba.protocol.xs2a.config.flowable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.variable.api.types.ValueFields;
import org.flowable.variable.service.impl.types.SerializableType;

import java.util.List;
import java.util.Map;

// TODO: Secure serialized values with some encryption, remove code duplication.
@RequiredArgsConstructor
class LargeJsonCustomSerializer extends SerializableType {

    static final String JSON = "as_large_json";

    private final ObjectMapper mapper;
    private final List<String> allowOnlyClassesWithPrefix;
    private final int minLength;

    @Override
    public String getTypeName() {
        return JSON;
    }

    @Override
    public boolean isCachable() {
        return true;
    }

    @Override
    @SneakyThrows
    public boolean isAbleToStore(Object o) {
        if (o == null) {
            return true;
        }

        if (!CanSerializeUtil.canSerialize(o.getClass().getCanonicalName(), allowOnlyClassesWithPrefix)) {
            return false;
        }

        String value = mapper.writeValueAsString(o);
        return value.length() > minLength;
    }

    @Override
    @SneakyThrows
    public byte[] serialize(Object o, ValueFields valueFields) {
        if (o == null) {
            return null;
        }

        return mapper.writeValueAsBytes(
                ImmutableMap.of(
                        o.getClass().getCanonicalName(),
                        o
                )
        );
    }

    @Override
    @SneakyThrows
    public Object deserialize(byte[] bytes, ValueFields valueFields) {
        if (null == bytes) {
            return null;
        }

        JsonNode value = mapper.readTree(bytes);
        Map.Entry<String, JsonNode> classNameAndValue = value.fields().next();

        if (!CanSerializeUtil.canSerialize(classNameAndValue.getKey(), allowOnlyClassesWithPrefix)) {
            throw new IllegalArgumentException("Class deserialization not allowed " + classNameAndValue.getKey());
        }

        return mapper.readValue(
                classNameAndValue.getValue().traverse(),
                Class.forName(classNameAndValue.getKey())
        );
    }
}

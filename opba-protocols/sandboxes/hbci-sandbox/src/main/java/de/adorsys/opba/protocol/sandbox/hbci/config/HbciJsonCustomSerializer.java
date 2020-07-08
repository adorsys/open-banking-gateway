package de.adorsys.opba.protocol.sandbox.hbci.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.variable.api.types.ValueFields;
import org.flowable.variable.api.types.VariableType;

import java.util.Collections;
import java.util.Map;

/**
 * JSON serializer for small classes (small resulting strings). Preserves the class name used, so deserialzation
 * returns the class that was used to serialize data.
 */
@RequiredArgsConstructor
public class HbciJsonCustomSerializer implements VariableType {

    static final String JSON = "as_hbci_sandbox_json";
    private final ObjectMapper mapper;

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
        return o instanceof HbciSandboxContext;
    }

    @Override
    @SneakyThrows
    public void setValue(Object o, ValueFields valueFields) {
        if (o == null) {
            valueFields.setBytes(null);
            return;
        }

        Map<String, Object> objectAndClass = Collections.singletonMap(o.getClass().getCanonicalName(), o);
        valueFields.setBytes(mapper.writeValueAsBytes(objectAndClass));
    }

    @Override
    @SneakyThrows
    public Object getValue(ValueFields valueFields) {
        if (null == valueFields.getBytes()) {
            return null;
        }

        JsonNode data = mapper.readTree(valueFields.getBytes());
        Map.Entry<String, JsonNode> classNameAndValue = data.fields().next();
        Class<?> dataClazz = Class.forName(classNameAndValue.getKey());
        return mapper.readValue(classNameAndValue.getValue().traverse(), dataClazz);
    }
}

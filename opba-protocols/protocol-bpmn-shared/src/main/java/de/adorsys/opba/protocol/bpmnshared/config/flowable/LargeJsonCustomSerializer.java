package de.adorsys.opba.protocol.bpmnshared.config.flowable;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.protocol.api.services.scoped.RequestScopedServicesProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.variable.api.types.ValueFields;
import org.flowable.variable.service.impl.types.SerializableType;

import java.util.List;

/**
 * JSON serializer for large classes (large resulting strings). Preserves the class name used, so deserialzation
 * returns the class that was used to serialize data.
 * Data is encrypted using {@link RequestScopedServicesProvider}.
 */
@RequiredArgsConstructor
public class LargeJsonCustomSerializer extends SerializableType {

    static final String JSON = "as_large_json";

    private final RequestScopedServicesProvider scopedServicesProvider;
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

        if (!SerializerUtil.canSerialize(o.getClass().getCanonicalName(), allowOnlyClassesWithPrefix)) {
            return false;
        }

        String value = mapper.writeValueAsString(o);
        return value.length() > minLength;
    }

    @Override
    public byte[] serialize(Object o, ValueFields valueFields) {
        if (o == null) {
            return null;
        }

        return SerializerUtil.serialize(o, mapper);
    }

    @Override
    public Object deserialize(byte[] bytes, ValueFields valueFields) {
        if (null == bytes) {
            return null;
        }

        return SerializerUtil.deserialize(
                bytes,
                mapper,
                allowOnlyClassesWithPrefix,
                scopedServicesProvider
        );
    }
}

package de.adorsys.opba.protocol.xs2a.config.flowable;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.protocol.api.services.ProtocolFacingEncryptionServiceProvider;
import de.adorsys.opba.protocol.xs2a.service.storage.TransientDataStorage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.variable.api.types.ValueFields;
import org.flowable.variable.service.impl.types.SerializableType;

import java.util.List;

// TODO: Secure serialized values with some encryption, remove code duplication.
@RequiredArgsConstructor
class LargeJsonCustomSerializer extends SerializableType {

    static final String JSON = "as_large_json";

    private final ProtocolFacingEncryptionServiceProvider encryptionServiceProvider;
    private final TransientDataStorage transientDataStorage;
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
                transientDataStorage,
                encryptionServiceProvider
        );
    }
}

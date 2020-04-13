package de.adorsys.opba.protocol.xs2a.config.flowable;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.protocol.api.services.ProtocolFacingEncryptionServiceProvider;
import de.adorsys.opba.protocol.xs2a.service.storage.TransientDataStorage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.variable.api.types.ValueFields;
import org.flowable.variable.api.types.VariableType;

import java.util.List;

// TODO: Secure serialized values with some encryption, remove code duplication.
@RequiredArgsConstructor
class JsonCustomSerializer implements VariableType {

    static final String JSON = "as_json";

    private final ProtocolFacingEncryptionServiceProvider encryptionServiceProvider;
    private final TransientDataStorage transientDataStorage;
    private final ObjectMapper mapper;
    private final List<String> allowOnlyClassesWithPrefix;
    private final int maxLength;

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
        return value.length() < maxLength;
    }

    @Override
    @SneakyThrows
    public void setValue(Object o, ValueFields valueFields) {
        if (o == null) {
            valueFields.setBytes(null);
            return;
        }

        valueFields.setBytes(SerializerUtil.serialize(o, mapper));
    }

    @Override
    @SneakyThrows
    public Object getValue(ValueFields valueFields) {
        if (null == valueFields.getBytes()) {
            return null;
        }

        return SerializerUtil.deserialize(
                valueFields.getBytes(),
                mapper,
                allowOnlyClassesWithPrefix,
                transientDataStorage,
                encryptionServiceProvider
        );
    }
}

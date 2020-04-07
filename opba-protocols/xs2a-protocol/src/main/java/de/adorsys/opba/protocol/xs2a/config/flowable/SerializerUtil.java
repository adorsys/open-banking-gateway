package de.adorsys.opba.protocol.xs2a.config.flowable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.xs2a.service.storage.EncryptionServiceProvider;
import de.adorsys.opba.protocol.xs2a.service.storage.NeedsTransientStorage;
import de.adorsys.opba.protocol.xs2a.service.storage.PersistenceShouldUseEncryption;
import de.adorsys.opba.protocol.xs2a.service.storage.TransientDataStorage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class SerializerUtil {

    public boolean canSerialize(String canonicalName, List<String> allowOnlyClassesWithPrefix) {
        return allowOnlyClassesWithPrefix.stream().anyMatch(canonicalName::startsWith);
    }

    @SneakyThrows
    public byte[] serialize(@NotNull Object data, @NotNull ObjectMapper mapper) {
        if (data instanceof PersistenceShouldUseEncryption) {
            return mapper.writeValueAsBytes(
                    ImmutableMap.of(
                            data.getClass().getCanonicalName(),
                            new EncryptedContainer((PersistenceShouldUseEncryption) data, mapper)
                    )
            );
        }

        return mapper.writeValueAsBytes(
                ImmutableMap.of(
                        data.getClass().getCanonicalName(),
                        mapper.writeValueAsString(data)
                )
        );
    }

    @SneakyThrows
    public Object deserialize(@NotNull byte[] bytes, @NotNull ObjectMapper mapper, @NotNull List<String> allowOnlyClassesWithPrefix,
                              @NotNull TransientDataStorage transientDataStorage, @NotNull EncryptionServiceProvider encryptionService) {
        JsonNode value = mapper.readTree(bytes);
        Map.Entry<String, JsonNode> classNameAndValue = value.fields().next();

        if (!canSerialize(classNameAndValue.getKey(), allowOnlyClassesWithPrefix)) {
            throw new IllegalArgumentException("Class deserialization not allowed " + classNameAndValue.getKey());
        }

        Class<?> dataClazz = Class.forName(classNameAndValue.getKey());
        Object result;

        if (PersistenceShouldUseEncryption.class.isAssignableFrom(dataClazz)) {
            EncryptedContainer container = mapper.readValue(classNameAndValue.getValue().traverse().getBinaryValue(), EncryptedContainer.class);
            result = mapper.readValue(
                    encryptionService.get(container.getEncId()).decrypt(container.getData()),
                    dataClazz
            );
        } else {
            result = mapper.readValue(
                    classNameAndValue.getValue().traverse().getBinaryValue(),
                    dataClazz
            );
        }

        if (result instanceof NeedsTransientStorage) {
            ((NeedsTransientStorage) result).setTransientStorage(transientDataStorage);
        }

        return result;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class EncryptedContainer {

        private String encId;
        private byte[] data;

        @SneakyThrows
        EncryptedContainer(PersistenceShouldUseEncryption entity, ObjectMapper mapper) {
            this.encId = entity.getEncryption().id();
            this.data = entity.getEncryption().encrypt(mapper.writeValueAsBytes(entity));
        }
    }
}

package de.adorsys.opba.protocol.xs2a.config.flowable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.api.services.scoped.RequestScopedServicesProvider;
import de.adorsys.opba.protocol.api.services.scoped.UsesRequestScoped;
import de.adorsys.opba.protocol.api.services.scoped.encryption.UsesEncryptionService;
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
        if (!(data instanceof UsesEncryptionService)) {
            throw new IllegalStateException("Can't serialize non-encrypted persistence classes");
        }

        return mapper.writeValueAsBytes(
                ImmutableMap.of(
                        data.getClass().getCanonicalName(),
                        new EncryptedContainer((UsesEncryptionService) data, mapper)
                )
        );
    }

    @SneakyThrows
    public Object deserialize(@NotNull byte[] bytes, @NotNull ObjectMapper mapper, @NotNull List<String> allowOnlyClassesWithPrefix,
                              @NotNull RequestScopedServicesProvider requestScoped) {
        JsonNode value = mapper.readTree(bytes);
        Map.Entry<String, JsonNode> classNameAndValue = value.fields().next();

        if (!canSerialize(classNameAndValue.getKey(), allowOnlyClassesWithPrefix)) {
            throw new IllegalArgumentException("Class deserialization not allowed " + classNameAndValue.getKey());
        }

        Class<?> dataClazz = Class.forName(classNameAndValue.getKey());
        Object result;

        if (!UsesEncryptionService.class.isAssignableFrom(dataClazz)) {
            throw new IllegalStateException("Can't deserialize non-encrypted persistence classes");
        }

        EncryptedContainer container = mapper.readValue(classNameAndValue.getValue().traverse(), EncryptedContainer.class);
        RequestScoped services = requestScoped.byEncryptionKeyId(container.getEncKeyId());

        if (null == services) {
            throw new IllegalStateException("Missing request scoped service for key: " + container.getEncKeyId());
        }

        result = mapper.readValue(
                services.encryption().decrypt(container.getData()),
                dataClazz
        );

        if (result instanceof UsesRequestScoped) {
            ((UsesRequestScoped) result).setRequestScoped(services);
        }

        return result;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class EncryptedContainer {

        private String encKeyId;
        private byte[] data;

        @SneakyThrows
        EncryptedContainer(UsesEncryptionService encryptionAccessor, ObjectMapper mapper) {
            if (null == encryptionAccessor.encryption()) {
                throw new IllegalStateException("Missing encryption service");
            }

            this.encKeyId = encryptionAccessor.encryption().getId();
            this.data = encryptionAccessor.encryption().encrypt(mapper.writeValueAsBytes(encryptionAccessor));
        }
    }
}

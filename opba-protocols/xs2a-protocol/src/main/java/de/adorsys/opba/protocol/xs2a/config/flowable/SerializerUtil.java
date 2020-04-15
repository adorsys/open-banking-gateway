package de.adorsys.opba.protocol.xs2a.config.flowable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.services.scoped.RequestScoped;
import de.adorsys.opba.protocol.api.services.scoped.RequestScopedServicesProvider;
import de.adorsys.opba.protocol.api.services.scoped.UsesRequestScoped;
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
        if (!(data instanceof RequestScoped)) {
            throw new IllegalStateException("Can't serialize non-request scoped classes");
        }

        return mapper.writeValueAsBytes(
                ImmutableMap.of(
                        data.getClass().getCanonicalName(),
                        new EncryptedContainer((RequestScoped) data, mapper)
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

        if (!RequestScoped.class.isAssignableFrom(dataClazz)) {
            throw new IllegalStateException("Can't deserialize non-request scoped classes");
        }

        EncryptedContainer container = mapper.readValue(classNameAndValue.getValue().traverse(), EncryptedContainer.class);
        RequestScoped services = requestScoped.findRegisteredByKeyId(container.getEncKeyId());

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
    private class EncryptedContainer {

        private String encKeyId;
        private byte[] data;

        @SneakyThrows
        EncryptedContainer(RequestScoped requestScoped, ObjectMapper mapper) {
            if (null == requestScoped || null == requestScoped.encryption()) {
                throw new IllegalStateException("Missing encryption service");
            }

            this.encKeyId = requestScoped.getEncryptionKeyId();
            this.data = requestScoped.encryption().encrypt(mapper.writeValueAsBytes(requestScoped));
        }
    }
}

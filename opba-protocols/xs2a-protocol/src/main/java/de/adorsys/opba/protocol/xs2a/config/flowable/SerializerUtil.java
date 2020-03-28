package de.adorsys.opba.protocol.xs2a.config.flowable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.protocol.xs2a.service.storage.NeedsTransientStorage;
import de.adorsys.opba.protocol.xs2a.service.storage.TransientDataStorage;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class SerializerUtil {

    public boolean canSerialize(String canonicalName, List<String> allowOnlyClassesWithPrefix) {
        return allowOnlyClassesWithPrefix.stream().anyMatch(canonicalName::startsWith);
    }

    public Object deserialize(byte[] bytes, ObjectMapper mapper, List<String> allowOnlyClassesWithPrefix, TransientDataStorage transientDataStorage) throws IOException, ClassNotFoundException {
        JsonNode value = mapper.readTree(bytes);
        Map.Entry<String, JsonNode> classNameAndValue = value.fields().next();

        if (!canSerialize(classNameAndValue.getKey(), allowOnlyClassesWithPrefix)) {
            throw new IllegalArgumentException("Class deserialization not allowed " + classNameAndValue.getKey());
        }

        Object result = mapper.readValue(
                classNameAndValue.getValue().traverse(),
                Class.forName(classNameAndValue.getKey())
        );

        if (result instanceof NeedsTransientStorage) {
            ((NeedsTransientStorage) result).setTransientStorage(transientDataStorage);
        }

        return result;
    }
}

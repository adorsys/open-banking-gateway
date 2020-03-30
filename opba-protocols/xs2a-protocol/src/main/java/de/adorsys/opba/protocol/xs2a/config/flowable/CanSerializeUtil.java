package de.adorsys.opba.protocol.xs2a.config.flowable;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class CanSerializeUtil {

    public boolean canSerialize(String canonicalName, List<String> allowOnlyClassesWithPrefix) {
        return allowOnlyClassesWithPrefix.stream().anyMatch(canonicalName::startsWith);
    }
}

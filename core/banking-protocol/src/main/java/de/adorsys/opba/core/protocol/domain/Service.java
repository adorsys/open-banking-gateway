package de.adorsys.opba.core.protocol.domain;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Service {
    ACCOUNTS("List accounts"),
    TRANSACTIONS("List transactions"),
    PAYMENT("Initiate payment");

    private static final Map<String, Service> codeIndex = Maps.newHashMapWithExpectedSize(Service.values().length);
    static {
        for (Service status : Service.values()) {
            codeIndex.put(status.getCode().toLowerCase(), status);
        }
    }
    public static Optional<Service> lookupByCode(String name) {
        return Optional.ofNullable(codeIndex.get(name.trim().toLowerCase()));
    }

    private String code;
}

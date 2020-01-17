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

    private static final Map<String, Service> CODE_INDEX = Maps.newHashMapWithExpectedSize(Service.values().length);
    static {
        for (Service status : Service.values()) {
            CODE_INDEX.put(status.getCode().toLowerCase(), status);
        }
    }
    public static Optional<Service> lookupByCode(String name) {
        return Optional.ofNullable(CODE_INDEX.get(name.trim().toLowerCase()));
    }

    private String code;
}

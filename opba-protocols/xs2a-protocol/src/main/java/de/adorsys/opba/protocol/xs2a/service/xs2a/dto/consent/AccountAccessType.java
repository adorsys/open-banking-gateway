package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum AccountAccessType {
    ALL_ACCOUNTS("ALL_ACCOUNTS"),
    ALL_ACCOUNTS_WITH_BALANCES("ALL_ACCOUNTS_WITH_BALANCES");

    private static Map<String, AccountAccessType> container = new HashMap<>();

    static {
        Arrays.stream(values())
                .forEach(aat -> container.put(aat.getDescription(), aat));
    }

    private String description;

    AccountAccessType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

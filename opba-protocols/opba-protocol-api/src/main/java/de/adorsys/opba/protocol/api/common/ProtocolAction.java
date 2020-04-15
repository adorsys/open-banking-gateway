package de.adorsys.opba.protocol.api.common;

import lombok.Getter;

public enum ProtocolAction {
    // AIS
    LIST_ACCOUNTS("list-accounts"),
    LIST_TRANSACTIONS("list-transactions"),
    // Consent
    AUTHORIZATION("authorization"),
    GET_AUTHORIZATION_STATE("get-authorization-state", AUTHORIZATION),
    UPDATE_AUTHORIZATION("update-authorization", AUTHORIZATION),
    DENY_AUTHORIZATION("deny-authorization", AUTHORIZATION),
    FROM_ASPSP_REDIRECT("from-aspsp", AUTHORIZATION),
    // PIS
    INITIATE_PAYMENT("initiate-payment");

    private final String name;

    @Getter
    private final ProtocolAction parent;

    ProtocolAction(String name) {
        this.name = name;
        this.parent = null;
    }

    ProtocolAction(String name, ProtocolAction parent) {
        this.name = name;
        this.parent = parent;
    }

    @Override
    public String toString() {
        return name;
    }
}

package de.adorsys.opba.db.domain.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ProtocolAction {
    // AIS
    LIST_ACCOUNTS("list-accounts"),
    LIST_TRANSACTIONS("list-transactions"),
    // Consent
    START_AUTHORIZATION("start-authorization"),
    UPDATE_AUTHORIZATION("update-authorization"),
    FROM_ASPSP_REDIRECT("from-aspsp"),
    // PIS
    INITIATE_PAYMENT("initiate-payment");

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}

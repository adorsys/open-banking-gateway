package de.adorsys.opba.db.domain.entity;

public enum ProtocolAction {
    // AIS
    LIST_ACCOUNTS,
    LIST_TRANSACTIONS,
    // Consent
    UPDATE_AUTHORIZATION,
    FROM_ASPSP_REDIRECT,
    // PIS
    INITIATE_PAYMENT
}

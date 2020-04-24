package de.adorsys.opba.protocol.api.common;

import lombok.Getter;

/**
 * All actions that can be done by {@code de.adorsys.opba.db.domain.entity.BankProtocol}. All of them are associated with some
 * class in <b>{@code opba-protocols/opba-protocol-api}</b> folder.
 */
public enum ProtocolAction {
    // AIS
    /**
     * {@code de.adorsys.opba.protocol.api.ais.ListAccounts}
     */
    LIST_ACCOUNTS("list-accounts"),
    /**
     * {@code de.adorsys.opba.protocol.api.ais.ListTransactions}
     */
    LIST_TRANSACTIONS("list-transactions"),
    // Consent
    /**
     * Root action for all authorizations
     */
    AUTHORIZATION("authorization"),
    /**
     * {@code de.adorsys.opba.protocol.api.authorization.GetAuthorizationState}
     */
    GET_AUTHORIZATION_STATE("get-authorization-state", AUTHORIZATION),
    /**
     * {@code de.adorsys.opba.protocol.api.authorization.UpdateAuthorization}
     */
    UPDATE_AUTHORIZATION("update-authorization", AUTHORIZATION),
    /**
     * {@code de.adorsys.opba.protocol.api.authorization.DenyAuthorization}
     */
    DENY_AUTHORIZATION("deny-authorization", AUTHORIZATION),
    /**
     * {@code de.adorsys.opba.protocol.api.authorization.FromAspspRedirect}
     */
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

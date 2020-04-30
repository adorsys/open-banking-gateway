package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent;

/**
 * Account/transactions scope access for Global consents.
 */
public enum AccountAccessType {
    ALL_ACCOUNTS("ALL_ACCOUNTS"),
    ALL_ACCOUNTS_WITH_BALANCES("ALL_ACCOUNTS_WITH_BALANCES");

    private String description;

    AccountAccessType(String description) {
        this.description = description;
    }

    public String getApiName() {
        return description;
    }
}

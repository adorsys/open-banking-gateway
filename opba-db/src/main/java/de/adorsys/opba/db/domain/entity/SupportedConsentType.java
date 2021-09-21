package de.adorsys.opba.db.domain.entity;

/**
 * The type of the consent that is supported by the bank (the widest one in permission terms).
 */
public enum SupportedConsentType {

    /**
     * Dedicated consent for account that provides account information, transactions, balance.
     */
    DEDICATED_ALL,
    /**
     * Dedicated consent for all accounts that provides account information, transactions, balance.
     */
    GLOBAL_ALL,
    /**
     * Dedicated consent for all accounts that provides account information.
     */
    GLOBAL_ACCOUNTS,
}

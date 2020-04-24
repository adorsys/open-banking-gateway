package de.adorsys.opba.protocol.api.dto.request.authorization;

import lombok.Data;

import java.util.List;

/**
 * AIS account consent access object. Defines the access scope to user accounts and transactions.
 */
@Data
public class AisAccountAccess {

    /**
     * List of account IBANs to be available for listing (i.e. will be returned on
     * {@link de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest}).
     */
    private List<String> accounts;

    /**
     * List of account IBANs to be available for listing with balances (i.e. will be returned on
     * {@link de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest}).
     */
    private List<String> balances;

    /**
     * List of account IBANs to be available for listing with balances and transactions associated to them
     * (i.e. will be returned on
     * {@link de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest}).
     */
    private List<String> transactions;

    /**
     * All PSD2 account access scope (allAccounts to have access to all accounts, transactions, balances).
     */
    private String allPsd2;

    /**
     * All account access scope (allAccounts to have access to all accounts without transactions and balances).
     */
    private String availableAccounts;
}

package de.adorsys.opba.protocol.api.dto.request.authorization;

import de.adorsys.opba.protocol.api.dto.result.body.AccountReference;
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
    private List<AccountReference> accounts;

    /**
     * List of account IBANs to be available for listing with balances (i.e. will be returned on
     * {@link de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest}).
     */
    private List<AccountReference> balances;

    /**
     * List of account IBANs to be available for listing with balances and transactions associated to them
     * (i.e. will be returned on
     * {@link de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest}).
     */
    private List<AccountReference> transactions;

    /**
     * All PSD2 account access scope (allAccounts to have access to all accounts, transactions, balances).
     */
    private String allPsd2;

    /**
     * All account access scope (allAccounts to have access to all accounts without transactions and balances).
     */
    private String availableAccounts;
}

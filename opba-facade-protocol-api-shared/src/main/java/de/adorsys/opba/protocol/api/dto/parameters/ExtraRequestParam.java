package de.adorsys.opba.protocol.api.dto.parameters;

/**
 * Additional parameters to extend {@link de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest}
 * and {@link de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest}.
 */
public enum ExtraRequestParam {
    CONSENT,
    IMPORT_DATA,
    PROTOCOL_CONFIGURATION
}

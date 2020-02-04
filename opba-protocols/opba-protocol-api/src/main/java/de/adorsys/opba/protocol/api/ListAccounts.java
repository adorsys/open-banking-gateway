package de.adorsys.opba.protocol.api;

import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;

@FunctionalInterface
public interface ListAccounts extends Action<ListAccountsRequest, AccountListBody> {
}

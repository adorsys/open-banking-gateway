package de.adorsys.opba.protocol.api;

import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;

@FunctionalInterface
public interface ListAccounts extends Action<ListAccountsRequest, AccountList> {
}

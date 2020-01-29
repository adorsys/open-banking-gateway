package de.adorsys.opba.protocol.api;

import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.Result;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface ListAccounts {

    CompletableFuture<Result<AccountList>> list(ListAccountsRequest request);
}

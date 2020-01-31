package de.adorsys.opba.protocol.xs2a.entrypoint.ais;

import de.adorsys.opba.protocol.api.ListAccounts;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.RedirectionResult;
import de.adorsys.opba.protocol.api.dto.result.Result;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service("xs2aListAccounts")
public class Xs2aListAccountsEntrypoint implements ListAccounts {

    @Override
    public CompletableFuture<Result<AccountList>> list(ListAccountsRequest request) {
        return CompletableFuture.completedFuture(new RedirectionResult<>());
    }
}

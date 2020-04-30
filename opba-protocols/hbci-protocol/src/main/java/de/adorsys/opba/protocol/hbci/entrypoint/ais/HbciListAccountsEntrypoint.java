package de.adorsys.opba.protocol.hbci.entrypoint.ais;

import de.adorsys.opba.protocol.api.ais.ListAccounts;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Entry point that handles ListAccounts request from the FinTech.
 */
@Service("hbciListAccounts")
@RequiredArgsConstructor
public class HbciListAccountsEntrypoint implements ListAccounts {

    @Override
    public CompletableFuture<Result<AccountListBody>> execute(ServiceContext<ListAccountsRequest> serviceContext) {
        return null;
    }
}

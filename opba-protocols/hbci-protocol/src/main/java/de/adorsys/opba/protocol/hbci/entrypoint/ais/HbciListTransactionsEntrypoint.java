package de.adorsys.opba.protocol.hbci.entrypoint.ais;

import de.adorsys.opba.protocol.api.ais.ListTransactions;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Entry point that handles ListTransactions request from the FinTech.
 */
@Service("hbciListTransactions")
@RequiredArgsConstructor
public class HbciListTransactionsEntrypoint implements ListTransactions {

    @Override
    public CompletableFuture<Result<TransactionsResponseBody>> execute(ServiceContext<ListTransactionsRequest> serviceContext) {
        return null;
    }
}

package de.adorsys.opba.protocol.xs2a.entrypoint.ais;

import de.adorsys.opba.protocol.api.ListTransactions;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.RedirectionResult;
import de.adorsys.opba.protocol.api.dto.result.Result;
import de.adorsys.opba.tppbankingapi.ais.model.generated.TransactionsResponse;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service("xs2aListTransactions")
public class Xs2aListTransactionsEntrypoint implements ListTransactions {

    @Override
    public CompletableFuture<Result<TransactionsResponse>> list(ListTransactionsRequest request) {
        return CompletableFuture.completedFuture(new RedirectionResult<>());
    }
}

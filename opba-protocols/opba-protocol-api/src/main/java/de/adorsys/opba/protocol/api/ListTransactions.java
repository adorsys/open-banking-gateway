package de.adorsys.opba.protocol.api;

import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.Result;
import de.adorsys.opba.tppbankingapi.ais.model.generated.TransactionsResponse;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface ListTransactions {

    CompletableFuture<Result<TransactionsResponse>> list(ServiceContext<ListTransactionsRequest> serviceContext);
}

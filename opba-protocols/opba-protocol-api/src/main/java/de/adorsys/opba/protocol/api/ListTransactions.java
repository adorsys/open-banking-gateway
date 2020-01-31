package de.adorsys.opba.protocol.api;

import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.tppbankingapi.ais.model.generated.TransactionsResponse;

@FunctionalInterface
public interface ListTransactions extends Action<ListTransactionsRequest, TransactionsResponse> {
}

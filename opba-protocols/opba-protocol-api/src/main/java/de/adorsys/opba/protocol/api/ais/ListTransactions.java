package de.adorsys.opba.protocol.api.ais;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;

@FunctionalInterface
public interface ListTransactions extends Action<ListTransactionsRequest, TransactionsResponseBody> {
}

package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.ListTransactions;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.Result;
import de.adorsys.opba.protocol.facade.services.RoutingService;
import de.adorsys.opba.tppbankingapi.ais.model.generated.TransactionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.LIST_TRANSACTIONS;

@Service
@RequiredArgsConstructor
public class ListTransactionsService {

    // bean name - bean-impl.
    private final Map<String, ? extends ListTransactions> transactionListProviders;
    private final RoutingService router;

    public CompletableFuture<Result<TransactionsResponse>> list(ListTransactionsRequest request) {
        return router.protocolFor(request.getBankID(), LIST_TRANSACTIONS, transactionListProviders).list(request);
    }
}

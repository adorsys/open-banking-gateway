package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.ListTransactions;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.Result;
import de.adorsys.opba.tppbankingapi.ais.model.generated.TransactionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ListTransactionsService {

    // bean name - bean-impl.
    private final Map<String, ? extends ListTransactions> accountListProviders;

    public CompletableFuture<Result<TransactionsResponse>> list(ListTransactionsRequest request) {
        // FIXME - xs2aListTransactions - This is a stub - waiting for 1st subtask (OBG-209):
        return accountListProviders.get("xs2aListTransactions").list(request);
    }
}

package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.db.repository.jpa.BankProtocolRepository;
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
    private final Map<String, ? extends ListTransactions> transactionListProviders;
    private final BankProtocolRepository protocolRepository;

    public CompletableFuture<Result<TransactionsResponse>> list(ListTransactionsRequest request) {
        return protocolRepository.findByBankProfileUuidAndAction(request.getBankID(), ProtocolAction.LIST_TRANSACTIONS)
                .map(protocol -> transactionListProviders.get(protocol.getProtocolBeanName()))
                .map(action -> action.list(request))
                .orElseThrow(() -> new IllegalStateException("No ais transaction list bean for " + request.getBankID()));
    }
}

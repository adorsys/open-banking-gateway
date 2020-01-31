package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.ListTransactions;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.ResultHandler;
import de.adorsys.opba.protocol.facade.services.ServiceContextProvider;
import de.adorsys.opba.tppbankingapi.ais.model.generated.TransactionsResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.LIST_TRANSACTIONS;

@Service
public class ListTransactionsService extends FacadeService<ListTransactionsRequest, TransactionsResponse, ListTransactions> {

    public ListTransactionsService(
            Map<String, ? extends ListTransactions> actionProviders,
            ProtocolSelector selector,
            ServiceContextProvider provider,
            ResultHandler handler) {
        super(LIST_TRANSACTIONS, actionProviders, selector, provider, handler);
    }
}

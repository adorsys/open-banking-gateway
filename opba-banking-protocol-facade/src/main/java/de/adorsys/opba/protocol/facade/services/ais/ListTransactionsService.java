package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.ais.ListTransactions;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.ServiceContextProvider;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.LIST_TRANSACTIONS;

@Service
public class ListTransactionsService extends FacadeService<ListTransactionsRequest, TransactionsResponseBody, ListTransactions> {

    public ListTransactionsService(
        Map<String, ? extends ListTransactions> actionProviders,
        ProtocolSelector selector,
        ServiceContextProvider provider,
        ProtocolResultHandler handler) {
        super(LIST_TRANSACTIONS, actionProviders, selector, provider, handler);
    }
}

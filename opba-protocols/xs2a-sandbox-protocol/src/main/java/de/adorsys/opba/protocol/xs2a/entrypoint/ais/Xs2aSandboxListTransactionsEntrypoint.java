package de.adorsys.opba.protocol.xs2a.entrypoint.ais;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.xs2a.entrypoint.ExtendWithServiceContext;
import de.adorsys.opba.protocol.xs2a.entrypoint.Xs2aResultBodyExtractor;
import de.adorsys.opba.protocol.xs2a.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.TransactionListXs2aContext;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("xs2aSandboxListTransactions")
public class Xs2aSandboxListTransactionsEntrypoint extends Xs2aListTransactionsEntrypoint {

    public Xs2aSandboxListTransactionsEntrypoint(RuntimeService runtimeService,
                                                 Xs2aResultBodyExtractor extractor,
                                                 ProcessEventHandlerRegistrar registrar,
                                                 FromRequest mapper,
                                                 ExtendWithServiceContext extender) {
        super(runtimeService, extractor, registrar, mapper, extender);
    }

    @Override
    protected TransactionListXs2aContext prepareContext(ServiceContext<ListTransactionsRequest> serviceContext) {
        TransactionListXs2aContext context = super.prepareContext(serviceContext);
        Map<ProtocolAction, String> flows = new HashMap<>(context.getFlowByAction());
        flows.put(ProtocolAction.LIST_TRANSACTIONS, "xs2a-sandbox-list-transactions");
        context.setFlowByAction(ImmutableMap.copyOf(flows));
        return context;
    }
}

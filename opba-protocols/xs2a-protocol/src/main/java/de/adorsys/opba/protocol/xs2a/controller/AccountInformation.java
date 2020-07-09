package de.adorsys.opba.protocol.xs2a.controller;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.protocol.xs2a.controller.constants.ApiPaths;
import de.adorsys.opba.protocol.xs2a.controller.constants.ApiVersion;
import de.adorsys.opba.protocol.xs2a.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.xs2a.service.xs2a.ContextFactory;
import de.adorsys.opba.protocol.xs2a.service.xs2a.Xs2aResultExtractor;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.model.AccountDetails;
import de.adorsys.xs2a.adapter.service.model.TransactionsReport;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.REQUEST_SAGA;

@RestController
@RequestMapping(ApiVersion.API_1)
@RequiredArgsConstructor
public class AccountInformation {

    private final RuntimeService runtimeService;
    private final ContextFactory contextFactory;
    private final Xs2aResultExtractor extractor;
    private final ProcessEventHandlerRegistrar registrar;

    @GetMapping(ApiPaths.ACCOUNTS)
    @Transactional
    public CompletableFuture<ResponseEntity<List<AccountDetails>>> accounts() {
        Xs2aContext context = contextFactory.createContext();
        context.setAction(ProtocolAction.LIST_ACCOUNTS);

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                REQUEST_SAGA,
                new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, context))
        );

        // TODO: Duplicated code
        CompletableFuture<ResponseEntity<List<AccountDetails>>> result = new CompletableFuture<>();

        registrar.addHandler(
                instance.getProcessInstanceId(),
                response -> result.complete(ResponseEntity.ok(extractor.extractAccountList(response))),
                result
        );

        return result;
    }

    // Use accountId received from /accounts
    @GetMapping(ApiPaths.TRANSACTIONS + "/{accountId}")
    @Transactional
    public CompletableFuture<ResponseEntity<TransactionsReport>> transactions(@PathVariable String accountId) {
        TransactionListXs2aContext context = contextFactory.createContextForTx();
        context.setAction(ProtocolAction.LIST_TRANSACTIONS);
        context.setResourceId(accountId);

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                REQUEST_SAGA,
                new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, context))
        );

        // TODO: Duplicated code
        CompletableFuture<ResponseEntity<TransactionsReport>> result = new CompletableFuture<>();

        registrar.addHandler(
                instance.getProcessInstanceId(),
                response -> result.complete(ResponseEntity.ok(extractor.extractTransactionsReport(response))),
                result
        );

        return result;
    }
}

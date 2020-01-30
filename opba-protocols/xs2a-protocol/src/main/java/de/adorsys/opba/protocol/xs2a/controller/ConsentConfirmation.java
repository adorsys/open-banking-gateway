package de.adorsys.opba.protocol.xs2a.controller;

import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.protocol.xs2a.controller.constants.ApiPaths;
import de.adorsys.opba.protocol.xs2a.controller.constants.ApiVersion;
import de.adorsys.opba.protocol.xs2a.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.xs2a.service.xs2a.Xs2aResultExtractor;
import de.adorsys.xs2a.adapter.service.model.AccountDetails;
import de.adorsys.xs2a.adapter.service.model.TransactionsReport;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ActivityInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(ApiVersion.API_1)
@RequiredArgsConstructor
@SuppressWarnings("CPD-START") // FIXME
public class ConsentConfirmation {

    private final RuntimeService runtimeService;
    private final Xs2aResultExtractor extractor;
    private final ProcessEventHandlerRegistrar registrar;


    @GetMapping(ApiPaths.CONSENTS + "/confirm/accounts/{action}/sagas/{sagaId}")
    @Transactional
    public CompletableFuture<? extends ResponseEntity<?>> confirmedRedirectConsentAccounts(
            @PathVariable ProtocolAction action,
            @PathVariable String sagaId) {

        runtimeService.trigger(executionToTrigger(sagaId));

        if (ProtocolAction.LIST_ACCOUNTS == action) {
            return accounts(sagaId);
        } else if (ProtocolAction.LIST_TRANSACTIONS == action) {
            return transactions(sagaId);
        }

        return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
    }

    private CompletableFuture<ResponseEntity<List<AccountDetails>>> accounts(String sagaId) {

        CompletableFuture<ResponseEntity<List<AccountDetails>>> result = new CompletableFuture<>();
        registrar.addHandler(
                sagaId,
                response -> result.complete(ResponseEntity.ok(extractor.extractAccountList(response))),
                result
        );
        return result;
    }

    private CompletableFuture<ResponseEntity<TransactionsReport>> transactions(String sagaId) {

        CompletableFuture<ResponseEntity<TransactionsReport>> result = new CompletableFuture<>();
        registrar.addHandler(
                sagaId,
                response -> result.complete(ResponseEntity.ok(extractor.extractTransactionsReport(response))),
                result
        );
        return result;
    }

    private String executionToTrigger(String sagaId) {
        String id = sagaId;
        ActivityInstance activity;
        // TODO limit depth... or find how to get execution immediately
        do {
            activity = runtimeService
                    .createActivityInstanceQuery()
                    .processInstanceId(id)
                    .unfinished()
                    .singleResult();
            id = activity.getCalledProcessInstanceId();
        } while (null != id);

        return activity.getExecutionId();
    }
}

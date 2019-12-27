package de.adorsys.opba.core.protocol.controller;

import de.adorsys.opba.core.protocol.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.core.protocol.service.xs2a.Xs2aResultExtractor;
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

import static de.adorsys.opba.core.protocol.controller.constants.ApiPaths.CONSENTS;
import static de.adorsys.opba.core.protocol.controller.constants.ApiVersion.API_1;

@RestController
@RequestMapping(API_1)
@RequiredArgsConstructor
public class ConsentConfirmation {

    private final RuntimeService runtimeService;
    private final Xs2aResultExtractor extractor;
    private final ProcessEventHandlerRegistrar registrar;


    // TODO: replace this hierarchy by waitng for some message Id instead
    @GetMapping(CONSENTS + "/confirm/accounts/{resultProcessId}/{consentProcessId}")
    @Transactional
    public CompletableFuture<ResponseEntity<List<AccountDetails>>> confirmedRedirectConsentAccounts(
            @PathVariable String resultProcessId, @PathVariable String consentProcessId) {

        ActivityInstance ai = runtimeService.createActivityInstanceQuery().processInstanceId(consentProcessId).unfinished().singleResult();
        runtimeService.trigger(ai.getExecutionId());

        CompletableFuture<ResponseEntity<List<AccountDetails>>> result = new CompletableFuture<>();

        registrar.addHandler(
                resultProcessId,
                response -> result.complete(ResponseEntity.ok(extractor.extractAccountList(response))),
                result
        );

        return result;
    }

    // TODO: replace this hierarchy by waitng for some message Id instead
    @GetMapping(CONSENTS + "/confirm/transactions/{resultProcessId}/{consentProcessId}")
    @Transactional
    public CompletableFuture<ResponseEntity<TransactionsReport>> confirmedRedirectConsentTransactions(
            @PathVariable String resultProcessId, @PathVariable String consentProcessId) {

        ActivityInstance ai = runtimeService.createActivityInstanceQuery().processInstanceId(consentProcessId).unfinished().singleResult();
        runtimeService.trigger(ai.getExecutionId());

        CompletableFuture<ResponseEntity<TransactionsReport>> result = new CompletableFuture<>();

        registrar.addHandler(
                resultProcessId,
                response -> result.complete(ResponseEntity.ok(extractor.extractTransactionsReport(response))),
                result
        );

        return result;
    }
}

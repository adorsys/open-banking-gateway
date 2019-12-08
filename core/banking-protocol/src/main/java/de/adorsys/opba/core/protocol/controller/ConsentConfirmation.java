package de.adorsys.opba.core.protocol.controller;

import de.adorsys.opba.core.protocol.domain.dto.RedirectResult;
import de.adorsys.opba.core.protocol.domain.dto.ResponseResult;
import de.adorsys.opba.core.protocol.service.eventbus.ProcessResultEventHandler;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.model.AccountDetails;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import de.adorsys.xs2a.adapter.service.model.TransactionsReport;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.core.protocol.controller.constants.ApiPaths.CONSENTS;
import static de.adorsys.opba.core.protocol.controller.constants.ApiVersion.API_1;

@RestController
@RequestMapping(API_1)
@RequiredArgsConstructor
public class ConsentConfirmation {

    private final RuntimeService runtimeService;
    private final ProcessResultEventHandler handler;


    // TODO: replace this hierarchy by waitng for some message Id instead
    @GetMapping(CONSENTS + "/confirm/accounts/{resultProcessId}/{consentProcessId}")
    @Transactional
    public CompletableFuture<ResponseEntity<List<AccountDetails>>> confirmedRedirectConsentAccounts(@PathVariable String resultProcessId, @PathVariable String consentProcessId) {

        ActivityInstance ai = runtimeService.createActivityInstanceQuery().processInstanceId(consentProcessId).unfinished().singleResult();
        runtimeService.trigger(ai.getExecutionId());

        CompletableFuture<ResponseEntity<List<AccountDetails>>> result = new CompletableFuture<>();

        // TODO Kill almost duplicate code - `handler.add`
        handler.add(
            resultProcessId,
            procResult -> {
                if (procResult instanceof ResponseResult) {
                    ProcessInstance updated = runtimeService.createProcessInstanceQuery().processInstanceId(procResult.getProcessId()).singleResult();
                    ExecutionEntity exec = (ExecutionEntity) updated;
                    result.complete(ResponseEntity.ok(
                        ((Xs2aContext) exec.getVariable(CONTEXT)).getResult(AccountListHolder.class).getAccounts()));
                } else if (procResult instanceof RedirectResult) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(URI.create(((RedirectResult) procResult).getRedirectUri()));
                    result.complete(new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY));
                } else {
                    result.complete(ResponseEntity.badRequest().build());
                }
            });

        return result;
    }

    // TODO: replace this hierarchy by waitng for some message Id instead
    @GetMapping(CONSENTS + "/confirm/transactions/{resultProcessId}/{consentProcessId}")
    @Transactional
    public CompletableFuture<ResponseEntity<TransactionsReport>> confirmedRedirectConsentTransactions(@PathVariable String resultProcessId, @PathVariable String consentProcessId) {

        ActivityInstance ai = runtimeService.createActivityInstanceQuery().processInstanceId(consentProcessId).unfinished().singleResult();
        runtimeService.trigger(ai.getExecutionId());

        CompletableFuture<ResponseEntity<TransactionsReport>> result = new CompletableFuture<>();

        // TODO Kill almost duplicate code - `handler.add`
        handler.add(
            resultProcessId,
            procResult -> {
                if (procResult instanceof ResponseResult) {
                    ProcessInstance updated = runtimeService.createProcessInstanceQuery().processInstanceId(procResult.getProcessId()).singleResult();
                    ExecutionEntity exec = (ExecutionEntity) updated;
                    result.complete(ResponseEntity.ok(
                        ((Xs2aContext) exec.getVariable(CONTEXT)).getResult(TransactionsReport.class)));
                } else if (procResult instanceof RedirectResult) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(URI.create(((RedirectResult) procResult).getRedirectUri()));
                    result.complete(new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY));
                } else {
                    result.complete(ResponseEntity.badRequest().build());
                }
            });

        return result;
    }
}

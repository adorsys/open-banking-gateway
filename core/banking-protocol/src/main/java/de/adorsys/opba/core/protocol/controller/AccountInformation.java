package de.adorsys.opba.core.protocol.controller;

import de.adorsys.opba.core.protocol.service.xs2a.ContextFactory;
import de.adorsys.opba.core.protocol.service.xs2a.context.TransactionListXs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.model.AccountDetails;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import de.adorsys.xs2a.adapter.service.model.TransactionsReport;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.core.protocol.controller.constants.ApiPaths.ACCOUNTS;
import static de.adorsys.opba.core.protocol.controller.constants.ApiPaths.TRANSACTIONS;
import static de.adorsys.opba.core.protocol.controller.constants.ApiVersion.API_1;

@RestController
@RequestMapping(API_1)
@RequiredArgsConstructor
public class AccountInformation {

    private final RuntimeService runtimeService;
    private final ContextFactory contextFactory;

    @GetMapping(ACCOUNTS)
    @Transactional
    public ResponseEntity<List<AccountDetails>> accounts() {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                "xs2a-ListAccounts",
                contextFactory.createXs2aContext()
        );

        ExecutionEntity exec = (ExecutionEntity) instance;
        return ResponseEntity.ok(
                ((Xs2aContext) exec.getVariable(CONTEXT)).getResult(AccountListHolder.class).getAccounts()
        );
    }

    @GetMapping(TRANSACTIONS)
    @Transactional
    public ResponseEntity<TransactionsReport> transactions() {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                "xs2a-ListTransactions",
                contextFactory.createXs2aContextForTx()
        );

        ExecutionEntity exec = (ExecutionEntity) instance;
        return ResponseEntity.ok(
                ((TransactionListXs2aContext) exec.getVariable(CONTEXT)).getResult(TransactionsReport.class)
        );
    }
}

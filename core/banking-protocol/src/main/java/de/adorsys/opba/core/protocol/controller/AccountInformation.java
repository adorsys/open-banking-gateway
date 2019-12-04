package de.adorsys.opba.core.protocol.controller;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.core.protocol.repository.jpa.BankConfigurationRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.core.protocol.controller.constants.ApiPaths.ACCOUNTS;
import static de.adorsys.opba.core.protocol.controller.constants.ApiPaths.TRANSACTIONS;
import static de.adorsys.opba.core.protocol.controller.constants.ApiVersion.API_1;
import static de.adorsys.opba.core.protocol.domain.entity.ProtocolAction.LIST_ACCOUNTS;
import static de.adorsys.opba.core.protocol.domain.entity.ProtocolAction.LIST_TRANSACTIONS;

@RestController
@RequestMapping(API_1)
@RequiredArgsConstructor
public class AccountInformation {

    private final RuntimeService runtimeService;
    private final ContextFactory contextFactory;
    private final BankConfigurationRepository config;

    @GetMapping(ACCOUNTS)
    @Transactional
    public ResponseEntity<List<AccountDetails>> accounts() {
        Xs2aContext context = contextFactory.createContext();

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                config.getOne(context.getBankConfigId()).getActions().get(LIST_ACCOUNTS).getProcessName(),
                new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, context))
        );

        ExecutionEntity exec = (ExecutionEntity) instance;
        return ResponseEntity.ok(
                ((Xs2aContext) exec.getVariable(CONTEXT)).getResult(AccountListHolder.class).getAccounts()
        );
    }

    // Use accountId received from /accounts
    @GetMapping(TRANSACTIONS + "/{accountId}")
    @Transactional
    public ResponseEntity<TransactionsReport> transactions(@PathVariable String accountId) {
        TransactionListXs2aContext context = contextFactory.createContextForTx();
        context.setResourceId(accountId);

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                config.getOne(context.getBankConfigId()).getActions().get(LIST_TRANSACTIONS).getProcessName(),
                new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, context))
        );

        ExecutionEntity exec = (ExecutionEntity) instance;
        return ResponseEntity.ok(
                ((TransactionListXs2aContext) exec.getVariable(CONTEXT)).getResult(TransactionsReport.class)
        );
    }
}

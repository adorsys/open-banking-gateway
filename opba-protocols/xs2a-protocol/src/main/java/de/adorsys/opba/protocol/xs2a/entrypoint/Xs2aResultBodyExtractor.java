package de.adorsys.opba.protocol.xs2a.entrypoint;

import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionListBody;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.InternalProcessResult;
import de.adorsys.xs2a.adapter.service.model.AccountDetails;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import de.adorsys.xs2a.adapter.service.model.TransactionsReport;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.List;

import static de.adorsys.opba.protocol.xs2a.service.ContextUtil.getResult;

@Service
@RequiredArgsConstructor
public class Xs2aResultBodyExtractor {

    private final RuntimeService runtimeService;

    @Deprecated // FIXME - kept only for tests using endpoints
    public List<AccountDetails> extractAccountListOld(InternalProcessResult result) {
        ProcessInstance updated =
                runtimeService.createProcessInstanceQuery()
                        .processInstanceId(result.getProcessId())
                        .singleResult();
        ExecutionEntity exec = (ExecutionEntity) updated;
        return getResult(exec, AccountListHolder.class).getAccounts();
    }

    @Deprecated // FIXME - kept only for tests using endpoints
    public TransactionsReport extractTransactionsReportOld(InternalProcessResult result) {
        ProcessInstance updated = runtimeService.createProcessInstanceQuery()
                .processInstanceId(result.getProcessId()).singleResult();
        ExecutionEntity exec = (ExecutionEntity) updated;
        return getResult(exec, TransactionsReport.class);
    }

    public AccountListBody extractAccountList(InternalProcessResult result) {
        ProcessInstance updated =
                runtimeService.createProcessInstanceQuery()
                        .processInstanceId(result.getProcessId())
                        .singleResult();
        ExecutionEntity exec = (ExecutionEntity) updated;
        return getResult(exec, AccountListBody.class);
    }

    public TransactionListBody extractTransactionsReport(InternalProcessResult result) {
        ProcessInstance updated = runtimeService.createProcessInstanceQuery()
                .processInstanceId(result.getProcessId()).singleResult();
        ExecutionEntity exec = (ExecutionEntity) updated;
        return getResult(exec, TransactionListBody.class);
    }
}

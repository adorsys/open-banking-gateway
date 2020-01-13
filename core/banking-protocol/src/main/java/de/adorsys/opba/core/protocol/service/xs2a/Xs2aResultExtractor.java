package de.adorsys.opba.core.protocol.service.xs2a;

import de.adorsys.opba.core.protocol.domain.dto.messages.ProcessResult;
import de.adorsys.xs2a.adapter.service.model.AccountDetails;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import de.adorsys.xs2a.adapter.service.model.TransactionsReport;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.List;

import static de.adorsys.opba.core.protocol.service.ContextUtil.getResult;

@Service
@RequiredArgsConstructor
public class Xs2aResultExtractor {

    private final RuntimeService runtimeService;

    public List<AccountDetails> extractAccountList(ProcessResult result) {
        ProcessInstance updated =
                runtimeService.createProcessInstanceQuery()
                        .processInstanceId(result.getProcessId())
                        .singleResult();
        ExecutionEntity exec = (ExecutionEntity) updated;
        return getResult(exec, AccountListHolder.class).getAccounts();
    }

    public TransactionsReport extractTransactionsReport(ProcessResult result) {
        ProcessInstance updated = runtimeService.createProcessInstanceQuery()
                .processInstanceId(result.getProcessId()).singleResult();
        ExecutionEntity exec = (ExecutionEntity) updated;
        return getResult(exec, TransactionsReport.class);
    }
}

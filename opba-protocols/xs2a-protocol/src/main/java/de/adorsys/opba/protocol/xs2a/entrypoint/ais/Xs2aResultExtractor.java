package de.adorsys.opba.protocol.xs2a.entrypoint.ais;

import de.adorsys.opba.protocol.xs2a.domain.dto.messages.ProcessResult;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import de.adorsys.opba.tppbankingapi.ais.model.generated.TransactionsResponse;
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
public class Xs2aResultExtractor {

    private final RuntimeService runtimeService;

    @Deprecated // FIXME - kept only for tests using endpoints
    public List<AccountDetails> extractAccountListOld(ProcessResult result) {
        ProcessInstance updated =
                runtimeService.createProcessInstanceQuery()
                        .processInstanceId(result.getProcessId())
                        .singleResult();
        ExecutionEntity exec = (ExecutionEntity) updated;
        return getResult(exec, AccountListHolder.class).getAccounts();
    }

    @Deprecated // FIXME - kept only for tests using endpoints
    public TransactionsReport extractTransactionsReportOld(ProcessResult result) {
        ProcessInstance updated = runtimeService.createProcessInstanceQuery()
                .processInstanceId(result.getProcessId()).singleResult();
        ExecutionEntity exec = (ExecutionEntity) updated;
        return getResult(exec, TransactionsReport.class);
    }

    public AccountList extractAccountList(ProcessResult result) {
        ProcessInstance updated =
                runtimeService.createProcessInstanceQuery()
                        .processInstanceId(result.getProcessId())
                        .singleResult();
        ExecutionEntity exec = (ExecutionEntity) updated;
        return getResult(exec, AccountList.class);
    }

    public TransactionsResponse extractTransactionsReport(ProcessResult result) {
        ProcessInstance updated = runtimeService.createProcessInstanceQuery()
                .processInstanceId(result.getProcessId()).singleResult();
        ExecutionEntity exec = (ExecutionEntity) updated;
        return getResult(exec, TransactionsResponse.class);
    }
}

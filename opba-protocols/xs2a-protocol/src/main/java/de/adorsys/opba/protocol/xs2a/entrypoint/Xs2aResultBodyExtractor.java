package de.adorsys.opba.protocol.xs2a.entrypoint;

import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.InternalProcessResult;
import de.adorsys.xs2a.adapter.service.model.AccountDetails;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import de.adorsys.xs2a.adapter.service.model.Transactions;
import de.adorsys.xs2a.adapter.service.model.TransactionsReport;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.ProcessInstance;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import java.util.List;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import static de.adorsys.opba.protocol.xs2a.service.ContextUtil.getResult;

@Service
@RequiredArgsConstructor
public class Xs2aResultBodyExtractor {

    private final RuntimeService runtimeService;
    private final Xs2aToFacadeMapper mapper;

    public AccountListBody extractAccountList(InternalProcessResult result) {
        ProcessInstance updated =
                runtimeService.createProcessInstanceQuery()
                        .processInstanceId(result.getProcessId())
                        .singleResult();
        ExecutionEntity exec = (ExecutionEntity) updated;

        return mapper.map(getResult(exec, AccountListHolder.class));
    }

    public TransactionsResponseBody extractTransactionsReport(InternalProcessResult result) {
        ProcessInstance updated = runtimeService.createProcessInstanceQuery()
                .processInstanceId(result.getProcessId()).singleResult();
        ExecutionEntity exec = (ExecutionEntity) updated;
        return mapper.map(getResult(exec, TransactionsReport.class));
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface Xs2aToFacadeMapper {
        AccountListBody map(AccountListHolder accountList);
        TransactionsResponseBody map(TransactionsReport transactions);
    }
}

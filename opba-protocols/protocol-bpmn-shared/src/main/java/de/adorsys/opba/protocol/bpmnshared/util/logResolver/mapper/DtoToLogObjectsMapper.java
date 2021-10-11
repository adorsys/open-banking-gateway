package de.adorsys.opba.protocol.bpmnshared.util.logResolver.mapper;

import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.bpmnshared.util.logResolver.domain.ContextLog;
import de.adorsys.opba.protocol.bpmnshared.util.logResolver.domain.ExecutionLog;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
public interface DtoToLogObjectsMapper {

    @Mapping(target = "lastRedirectTo", source = "context.lastRedirection.redirectTo")
    @Mapping(target = "lastRedirectToUiScreen", source = "context.lastRedirection.redirectToUiScreen")
    ContextLog mapFromContextDtoToContextLog(BaseContext context);

    ExecutionLog mapFromExecutionToExecutionLog(DelegateExecution execution);
}

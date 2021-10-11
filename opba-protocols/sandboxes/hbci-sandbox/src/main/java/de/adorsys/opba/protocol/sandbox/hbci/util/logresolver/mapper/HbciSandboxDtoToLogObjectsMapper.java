package de.adorsys.opba.protocol.sandbox.hbci.util.logresolver.mapper;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.util.logresolver.domain.HbciSandboxExecutionLog;
import de.adorsys.opba.protocol.sandbox.hbci.util.logresolver.domain.HbciSandboxContextLog;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;


@Mapper
public interface HbciSandboxDtoToLogObjectsMapper {

    HbciSandboxContextLog mapFromHbciSandboxContextDtoToHbciSandboxContextLog(HbciSandboxContext context);

    HbciSandboxExecutionLog mapFromExecutionToHbciSandboxExecutionLog(DelegateExecution execution);
}

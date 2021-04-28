package de.adorsys.opba.protocol.sandbox.hbci.util.logresolver;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.util.logresolver.mapper.HbciSandboxDtoToLogObjectsMapper;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HbciSandboxLogResolver {

    private final Logger log;
    private final HbciSandboxDtoToLogObjectsMapper mapper;

    public HbciSandboxLogResolver(Class<?> clazz) {
        log = LoggerFactory.getLogger(clazz);
        mapper = Mappers.getMapper(HbciSandboxDtoToLogObjectsMapper.class);
    }

    public void log(String message, Object... parameters) {
        log.info(message, parameters);
    }

    public void log(String message, DelegateExecution execution, HbciSandboxContext context) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromExecutionToHbciSandboxExecutionLog(execution),
                    mapper.mapFromHbciSandboxContextDtoToHbciSandboxContextLog(context)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromExecutionToHbciSandboxExecutionLog(execution),
                    mapper.mapFromHbciSandboxContextDtoToHbciSandboxContextLog(context).getNotSensitiveData()
            );
        }
    }

}

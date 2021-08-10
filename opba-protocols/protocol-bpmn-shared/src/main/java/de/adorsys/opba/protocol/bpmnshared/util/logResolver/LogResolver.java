package de.adorsys.opba.protocol.bpmnshared.util.logResolver;

import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.bpmnshared.util.logResolver.mapper.DtoToLogObjectsMapper;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogResolver<T extends BaseContext> {
    private final Logger log;
    private final DtoToLogObjectsMapper mapper;

    public LogResolver(Class<?> clazz) {
        log = LoggerFactory.getLogger(clazz);
        mapper = Mappers.getMapper(DtoToLogObjectsMapper.class);
    }

    public void log(String message, Object... parameters) {
        log.info(message, parameters);
    }

    public void log(String message, DelegateExecution execution, T context) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromExecutionToExecutionLog(execution),
                    mapper.mapFromContextDtoToContextLog(context)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromExecutionToExecutionLog(execution),
                    mapper.mapFromContextDtoToContextLog(context).getNotSensitiveData()
            );
        }
    }

}

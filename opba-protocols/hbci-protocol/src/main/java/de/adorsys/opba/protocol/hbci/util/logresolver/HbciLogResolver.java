package de.adorsys.opba.protocol.hbci.util.logresolver;

import de.adorsys.multibanking.domain.request.AbstractRequest;
import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.response.AbstractResponse;
import de.adorsys.multibanking.domain.transaction.AbstractTransaction;
import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.context.PaymentHbciContext;
import de.adorsys.opba.protocol.hbci.context.TransactionListHbciContext;
import de.adorsys.opba.protocol.hbci.util.logresolver.domain.request.RequestLog;
import de.adorsys.opba.protocol.hbci.util.logresolver.domain.request.TransactionRequestLog;
import de.adorsys.opba.protocol.hbci.util.logresolver.domain.response.ResponseLog;
import de.adorsys.opba.protocol.hbci.util.logresolver.mapper.HbciDtoToLogObjectsMapper;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HbciLogResolver<REQUEST extends AbstractRequest, RESPONSE extends AbstractResponse> {

    private final Logger log;
    private final HbciDtoToLogObjectsMapper mapper;

    public HbciLogResolver(Class<?> clazz) {
        log = LoggerFactory.getLogger(clazz);
        mapper = Mappers.getMapper(HbciDtoToLogObjectsMapper.class);
    }

    public void log(String message, Object... parameters) {
        log.debug(message, parameters);
    }

    public void log(String message, DelegateExecution execution) {
        log.info(message, mapper.mapFromExecutionToHbciExecutionLog(execution));
    }

    public void log(String message, DelegateExecution execution, BaseContext context) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromExecutionToHbciExecutionLog(execution),
                    mapper.mapBaseContextDtoToBaseContextLog(context)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromExecutionToHbciExecutionLog(execution),
                    mapper.mapBaseContextDtoToBaseContextLog(context).getNotSensitiveData()
            );
        }
    }

    public void log(String message, DelegateExecution execution, HbciContext context) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromExecutionToHbciExecutionLog(execution),
                    mapper.mapFromHbciContextDtoToHbciContextLog(context)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromExecutionToHbciExecutionLog(execution),
                    mapper.mapFromHbciContextDtoToHbciContextLog(context).getNotSensitiveData()
            );
        }
    }

    public void log(String message, DelegateExecution execution, AccountListHbciContext context) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromExecutionToHbciExecutionLog(execution),
                    mapper.mapFromAccountListHbciContextDtoToAccountListHbciContextLog(context)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromExecutionToHbciExecutionLog(execution),
                    mapper.mapFromAccountListHbciContextDtoToAccountListHbciContextLog(context).getNotSensitiveData());
        }
    }

    public void log(String message, DelegateExecution execution, PaymentHbciContext context) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromExecutionToHbciExecutionLog(execution),
                    mapper.mapFromPaymentHbciContextDtoToPaymentHbciContextLog(context)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromExecutionToHbciExecutionLog(execution),
                    mapper.mapFromPaymentHbciContextDtoToPaymentHbciContextLog(context).getNotSensitiveData()
            );
        }
    }

    public void log(String message, DelegateExecution execution, TransactionListHbciContext context) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromExecutionToHbciExecutionLog(execution),
                    mapper.mapFromTransactionListHbciContextDtoToTransactionListHbciContextLog(context)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromExecutionToHbciExecutionLog(execution),
                    mapper.mapFromTransactionListHbciContextDtoToTransactionListHbciContextLog(context).getNotSensitiveData()
            );
        }
    }

    public void log(String message, TransactionRequest<? extends AbstractTransaction> request) {
        TransactionRequestLog requestLog = new TransactionRequestLog(request);

        if (log.isDebugEnabled()) {
            log.debug(message, requestLog);
        } else {
            log.info(message, requestLog.getNotSensitiveData());
        }
    }

    public void log(String message, REQUEST request) {
        RequestLog<REQUEST> requestLog = new RequestLog<>(request);

        if (log.isDebugEnabled()) {
            log.debug(message, requestLog);
        } else {
            log.info(message, requestLog.getNotSensitiveData());
        }
    }

    public void log(String message, RESPONSE response) {
        ResponseLog<RESPONSE> responseLog = new ResponseLog<>(response);

        if (log.isDebugEnabled()) {
            log.debug(message, responseLog);
        } else {
            log.info(message, responseLog.getNotSensitiveData());
        }
    }

}

package de.adorsys.opba.protocol.hbci.util.logresolver;

import de.adorsys.multibanking.domain.request.SelectPsuAuthenticationMethodRequest;
import de.adorsys.multibanking.domain.request.TransactionAuthorisationRequest;
import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.request.UpdatePsuAuthenticationRequest;
import de.adorsys.multibanking.domain.response.AccountInformationResponse;
import de.adorsys.multibanking.domain.response.PaymentResponse;
import de.adorsys.multibanking.domain.response.PaymentStatusResponse;
import de.adorsys.multibanking.domain.response.TransactionsResponse;
import de.adorsys.multibanking.domain.response.UpdateAuthResponse;
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


public class HbciLogResolver {

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
        TransactionRequestLog requestLog = new TransactionRequestLog();
        requestLog.setRequest(request);

        if (log.isDebugEnabled()) {
            log.debug(message, requestLog);
        } else {
            log.info(message, requestLog.getNotSensitiveData());
        }
    }

    public void log(String message, UpdatePsuAuthenticationRequest request) {
        RequestLog<UpdatePsuAuthenticationRequest> requestLog = new RequestLog<>(request);

        if (log.isDebugEnabled()) {
            log.debug(message, requestLog);
        } else {
            log.info(message, requestLog.getNotSensitiveData());
        }
    }

    public void log(String message, TransactionAuthorisationRequest request) {
        RequestLog<TransactionAuthorisationRequest> requestLog = new RequestLog<>(request);

        if (log.isDebugEnabled()) {
            log.debug(message, requestLog);
        } else {
            log.info(message, requestLog.getNotSensitiveData());
        }
    }

    public void log(String message, SelectPsuAuthenticationMethodRequest request) {
        RequestLog<SelectPsuAuthenticationMethodRequest> requestLog = new RequestLog<>(request);

        if (log.isDebugEnabled()) {
            log.debug(message, requestLog);
        } else {
            log.info(message, requestLog.getNotSensitiveData());
        }
    }

    public void log(String message, AccountInformationResponse response) {
        ResponseLog<AccountInformationResponse> responseLog = new ResponseLog<>();
        responseLog.setResponse(response);

        if (log.isDebugEnabled()) {
            log.debug(message, responseLog);
        } else {
            log.info(message, responseLog.getNotSensitiveData());
        }
    }

    public void log(String message, UpdateAuthResponse response) {
        ResponseLog<UpdateAuthResponse> responseLog = new ResponseLog<>();
        responseLog.setResponse(response);

        if (log.isDebugEnabled()) {
            log.debug(message, responseLog);
        } else {
            log.info(message, responseLog.getNotSensitiveData());
        }
    }

    public void log(String message, PaymentResponse response) {
        ResponseLog<PaymentResponse> responseLog = new ResponseLog<>();
        responseLog.setResponse(response);

        if (log.isDebugEnabled()) {
            log.debug(message, responseLog);
        } else {
            log.info(message, responseLog.getNotSensitiveData());
        }
    }

    public void log(String message, TransactionsResponse response) {
        ResponseLog<TransactionsResponse> responseLog = new ResponseLog<>();
        responseLog.setResponse(response);

        if (log.isDebugEnabled()) {
            log.debug(message, responseLog);
        } else {
            log.info(message, responseLog.getNotSensitiveData());
        }
    }

    public void log(String message, PaymentStatusResponse response) {
        ResponseLog<PaymentStatusResponse> responseLog = new ResponseLog<>();
        responseLog.setResponse(response);

        if (log.isDebugEnabled()) {
            log.debug(message, responseLog);
        } else {
            log.info(message, responseLog.getNotSensitiveData());
        }
    }
}

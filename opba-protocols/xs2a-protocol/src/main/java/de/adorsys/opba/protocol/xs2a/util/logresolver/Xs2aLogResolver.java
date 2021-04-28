package de.adorsys.opba.protocol.xs2a.util.logresolver;

import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathQueryHeaders;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedQueryHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aInitialConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aInitialPaymentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aResourceParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStartPaymentAuthorizationParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aTransactionParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aWithBalanceParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aWithConsentIdHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.ConsentInitiateParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentInitiateHeaders;
import de.adorsys.opba.protocol.xs2a.util.logresolver.mapper.Xs2aDtoToLogObjectsMapper;
import de.adorsys.xs2a.adapter.api.model.Consents;
import de.adorsys.xs2a.adapter.api.model.PaymentInitiationJson;
import de.adorsys.xs2a.adapter.api.model.SelectPsuAuthenticationMethod;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Xs2aLogResolver {
    private final Logger log;
    private final Xs2aDtoToLogObjectsMapper mapper;

    public Xs2aLogResolver(Class<?> clazz) {
        log = LoggerFactory.getLogger(clazz);
        mapper = Mappers.getMapper(Xs2aDtoToLogObjectsMapper.class);
    }

//    public void log(String message, Object... parameters) {
//        if (log.isDebugEnabled()) {
//            log.debug(message, parameters);
//        } else {
//            log.info(message, parameters);
//        }
//    }

    public void log(String message, DelegateExecution execution) {
        log.info(
                message,
                mapper.mapFromExecutionToXs2aExecutionLog(execution)
        );
    }

    public void log(String message, DelegateExecution execution, BaseContext context) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromExecutionToXs2aExecutionLog(execution),
                    mapper.mapBaseContextDtoToBaseContextLog(context)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromExecutionToXs2aExecutionLog(execution),
                    mapper.mapBaseContextDtoToBaseContextLog(context).getNotSensitiveData()
            );
        }
    }

    public void log(String message, DelegateExecution execution, Xs2aContext context) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromExecutionToXs2aExecutionLog(execution),
                    mapper.mapFromXs2aContextDtoToXs2aContextLog(context)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromExecutionToXs2aExecutionLog(execution),
                    mapper.mapFromXs2aContextDtoToXs2aContextLog(context).getNotSensitiveData()
            );
        }
    }

    public void log(String message, DelegateExecution execution, TransactionListXs2aContext context) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromExecutionToXs2aExecutionLog(execution),
                    mapper.mapFromTransactionListXs2aContextDtoToXs2aContextLog(context)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromExecutionToXs2aExecutionLog(execution),
                    mapper.mapFromTransactionListXs2aContextDtoToXs2aContextLog(context).getNotSensitiveData()
            );
        }
    }

    public void log(String message, DelegateExecution execution, Xs2aPisContext context) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromExecutionToXs2aExecutionLog(execution),
                    mapper.mapFromXs2aPisContextDtoToXs2aPisContextLog(context)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromExecutionToXs2aExecutionLog(execution),
                    mapper.mapFromXs2aPisContextDtoToXs2aPisContextLog(context).getNotSensitiveData()
            );
        }
    }

    public void log(String message, ValidatedQueryHeaders<Xs2aWithBalanceParameters, Xs2aWithConsentIdHeaders> parameters) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromQueryHeadersDtoToQueryHeadersLog(parameters)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromQueryHeadersDtoToQueryHeadersLog(parameters).getNotSensitiveData()
            );
        }
    }

    public void log(String message, ValidatedPathQueryHeaders<Xs2aResourceParameters, Xs2aTransactionParameters, Xs2aWithConsentIdHeaders> parameters) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromPathQueryHeadersDtoToPathQueryHeadersLog(parameters)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromPathQueryHeadersDtoToPathQueryHeadersLog(parameters).getNotSensitiveData()
            );
        }
    }

    public void log(String message, ValidatedPathHeadersBody<ConsentInitiateParameters, ConsentInitiateHeaders, Consents> parameters) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromPathHeadersBodyDtoToPathHeadersBodyLog(parameters)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromPathHeadersBodyDtoToPathHeadersBodyLog(parameters).getNotSensitiveData()
            );
        }
    }

    public void log(String message, Xs2aInitialPaymentParameters path, PaymentInitiateHeaders header, PaymentInitiationJson body) {
        if (log.isDebugEnabled()) {
             log.debug(
                     message,
                     mapper.mapFromPathHeadersBodyDtoToPisPathHeadersBodyLog(path, header, body)
             );
        } else {
            log.info(
                    message,
                    mapper.mapFromPathHeadersBodyDtoToPisPathHeadersBodyLog(path, header, body).getNotSensitiveData()
            );
        }
    }

    public void log(String message, Xs2aInitialConsentParameters path, Xs2aStandardHeaders header) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromPathHeadersDtoToConsentPathHeadersLog(path, header)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromPathHeadersDtoToConsentPathHeadersLog(path, header).getNotSensitiveData()
            );
        }
    }

    public void log(String message, Xs2aStartPaymentAuthorizationParameters path, Xs2aStandardHeaders headers) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromPathHeadersDtoToPisPathHeadersLog(path, headers)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromPathHeadersDtoToPisPathHeadersLog(path, headers).getNotSensitiveData()
            );
        }
    }

    public void log(String message, Xs2aAuthorizedConsentParameters path, Xs2aStandardHeaders headers, SelectPsuAuthenticationMethod body) {
        if (log.isDebugEnabled()) {
            log.debug(
                    message,
                    mapper.mapFromPathHeadersBodyDtoToConsentPathHeadersBodyLog(path, headers, body)
            );
        } else {
            log.info(
                    message,
                    mapper.mapFromPathHeadersBodyDtoToConsentPathHeadersBodyLog(path, headers, body).getNotSensitiveData()
            );
        }
    }
}

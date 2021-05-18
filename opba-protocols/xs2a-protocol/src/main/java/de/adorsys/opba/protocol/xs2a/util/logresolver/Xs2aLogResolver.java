package de.adorsys.opba.protocol.xs2a.util.logresolver;

import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedPaymentParameters;
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
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2.Xs2aOauth2Headers;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2.Xs2aOauth2Parameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.oauth2.Xs2aOauth2WithCodeParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.payment.PaymentInitiateHeaders;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.response.ResponseLog;
import de.adorsys.opba.protocol.xs2a.util.logresolver.mapper.Xs2aDtoToLogObjectsMapper;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.Consents;
import de.adorsys.xs2a.adapter.api.model.PaymentInitiationJson;
import de.adorsys.xs2a.adapter.api.model.SelectPsuAuthenticationMethod;
import de.adorsys.xs2a.adapter.api.model.TokenResponse;
import de.adorsys.xs2a.adapter.api.model.TransactionAuthorisation;
import de.adorsys.xs2a.adapter.api.model.UpdatePsuAuthentication;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;


public class Xs2aLogResolver<T> {
    private final Logger log;
    private final Xs2aDtoToLogObjectsMapper mapper;

    public Xs2aLogResolver(Class<?> clazz) {
        log = LoggerFactory.getLogger(clazz);
        mapper = Mappers.getMapper(Xs2aDtoToLogObjectsMapper.class);
    }

    //executions+context
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

    //parameters
    public void log(String message, Xs2aWithBalanceParameters query, Xs2aWithConsentIdHeaders headers) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromQueryHeadersToXs2aValidatedQueryHeadersLog(query, headers));
        } else {
            log.info(message, mapper.mapFromQueryHeadersToXs2aValidatedQueryHeadersLog(query, headers).getNotSensitiveData());
        }
    }

    public void log(String message, Xs2aOauth2WithCodeParameters query, Xs2aOauth2Headers headers) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromQueryHeadersToXs2aValidatedQueryHeadersLog(query, headers));
        } else {
            log.info(message, mapper.mapFromQueryHeadersToXs2aValidatedQueryHeadersLog(query, headers).getNotSensitiveData());
        }
    }

    public void log(String message, Xs2aOauth2Parameters query, Xs2aOauth2Headers headers) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromQueryHeadersToXs2aValidatedQueryHeadersLog(query, headers));
        } else {
            log.info(message, mapper.mapFromQueryHeadersToXs2aValidatedQueryHeadersLog(query, headers).getNotSensitiveData());
        }
    }

    public void log(String message, Xs2aResourceParameters path, Xs2aTransactionParameters query, Xs2aWithConsentIdHeaders headers) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromPathQueryHeadersToXs2aValidatedPathQueryHeadersLog(path, query, headers));
        } else {
            log.info(message, mapper.mapFromPathQueryHeadersToXs2aValidatedPathQueryHeadersLog(path, query, headers).getNotSensitiveData()
            );
        }
    }

    public void log(String message, ConsentInitiateParameters path, ConsentInitiateHeaders headers, Consents body) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, headers, body)
            );
        } else {
            log.info(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, headers, body).getNotSensitiveData());
        }
    }

    public void log(String message, Xs2aInitialPaymentParameters path, PaymentInitiateHeaders header, PaymentInitiationJson body) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, header, body));
        } else {
            log.info(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, header, body).getNotSensitiveData());
        }
    }

    public void log(String message, Xs2aInitialConsentParameters path, Xs2aStandardHeaders header) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromPathHeadersToXs2aValidatedPathHeadersLog(path, header));
        } else {
            log.info(message, mapper.mapFromPathHeadersToXs2aValidatedPathHeadersLog(path, header).getNotSensitiveData());
        }
    }

    public void log(String message, Xs2aStartPaymentAuthorizationParameters path, Xs2aStandardHeaders headers) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromPathHeadersToXs2aValidatedPathHeadersLog(path, headers));
        } else {
            log.info(message, mapper.mapFromPathHeadersToXs2aValidatedPathHeadersLog(path, headers).getNotSensitiveData());
        }
    }

    public void log(String message, Xs2aAuthorizedConsentParameters path, Xs2aStandardHeaders headers, SelectPsuAuthenticationMethod body) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, headers, body));
        } else {
            log.info(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, headers, body).getNotSensitiveData());
        }
    }

    public void log(String message, Xs2aAuthorizedConsentParameters path, Xs2aStandardHeaders headers, TransactionAuthorisation body) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, headers, body));
        } else {
            log.info(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, headers, body).getNotSensitiveData());
        }
    }

    public void log(String message, Xs2aAuthorizedConsentParameters path, Xs2aStandardHeaders headers, UpdatePsuAuthentication body) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, headers, body));
        } else {
            log.info(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, headers, body).getNotSensitiveData());
        }
    }

    public void log(String message, Xs2aAuthorizedPaymentParameters path, Xs2aStandardHeaders headers, TransactionAuthorisation body) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, headers, body));
        } else {
            log.info(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, headers, body).getNotSensitiveData());
        }
    }

    public void log(String message, Xs2aAuthorizedPaymentParameters path, Xs2aStandardHeaders headers, UpdatePsuAuthentication body) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, headers, body));
        } else {
            log.info(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, headers, body).getNotSensitiveData());
        }
    }

    public void log(String message, Xs2aAuthorizedPaymentParameters path, Xs2aStandardHeaders headers, SelectPsuAuthenticationMethod body) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, headers, body));
        } else {
            log.info(message, mapper.mapFromPathHeadersBodyToXs2aValidatedPathHeadersBodyLog(path, headers, body).getNotSensitiveData());
        }
    }

    //responses
    public void log(String message, Response<T> response) {
        ResponseLog<T> responseLog = new ResponseLog<>();
        responseLog.setStatusCode(response.getStatusCode());
        responseLog.setHeaders(response.getHeaders());
        responseLog.setBody(response.getBody());

        if (log.isDebugEnabled()) {
            log.debug(message, responseLog);
        } else {
            log.info(message, responseLog.getNotSensitiveData());
        }
    }

    public void log(String message, TokenResponse response) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromTokenResponseToTokenResponseLog(response));
        } else {
            log.info(message, mapper.mapFromTokenResponseToTokenResponseLog(response).getNotSensitiveData());
        }
    }

    public void log(String message, URI response) {
        if (log.isDebugEnabled()) {
            log.debug(message, mapper.mapFromURIToURILog(response));
        } else {
            log.info(message, mapper.mapFromURIToURILog(response).getNotSensitiveData());
        }
    }

    public void log(String message, String response) {
        if (log.isDebugEnabled()) {
            log.debug(message, response);
        } else {
            log.info(message, "****");
        }
    }
}

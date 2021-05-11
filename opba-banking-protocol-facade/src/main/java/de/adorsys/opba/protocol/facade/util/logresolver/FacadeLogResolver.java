package de.adorsys.opba.protocol.facade.util.logresolver;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.result.body.ResultBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.facade.services.ProtocolWithCtx;
import de.adorsys.opba.protocol.facade.util.logresolver.domain.ActionLog;
import de.adorsys.opba.protocol.facade.util.logresolver.domain.ProtocolWithCtxLog;
import de.adorsys.opba.protocol.facade.util.logresolver.domain.request.FacadeServiceableRequestLog;
import de.adorsys.opba.protocol.facade.util.logresolver.domain.request.RequestLog;
import de.adorsys.opba.protocol.facade.util.logresolver.domain.context.ServiceContextLog;
import de.adorsys.opba.protocol.facade.util.logresolver.domain.response.ResultLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static de.adorsys.opba.protocol.facade.util.logresolver.Constants.NULL;


public class FacadeLogResolver<REQUEST extends FacadeServiceableGetter, RESULT extends Result, RESULTBODY extends ResultBody, ACTION extends Action<REQUEST, RESULTBODY>> {

    private final Logger log;

    public FacadeLogResolver(Class<?> clazz) {
        log = LoggerFactory.getLogger(clazz);
    }

    public void log(String message, REQUEST request) {
        RequestLog<REQUEST> requestLog = new RequestLog<>(request);

        if (log.isDebugEnabled()) {
            log.debug(message, requestLog);
        } else {
            log.info(message, requestLog.getNotSensitiveData());
        }
    }

    public void log(String message, RESULT result, FacadeServiceableRequest request, ServiceContext<REQUEST> ctx) {
        ResultLog<RESULT> resultLog = new ResultLog<>(result);
        FacadeServiceableRequestLog requestLog = new FacadeServiceableRequestLog(request);
        ServiceContextLog<REQUEST> contextLog = new ServiceContextLog<>(ctx != null ? ctx.getCtx() : null);

        if (log.isDebugEnabled()) {
            log.debug(message, resultLog, requestLog, contextLog);
        } else {
            log.info(message, resultLog.getNotSensitiveData(), requestLog.getNotSensitiveData(), contextLog.getNotSensitiveData());
        }
    }

    public void log(String message, ProtocolWithCtx<ACTION, REQUEST> protocolWithCtx) {
        if (protocolWithCtx == null) {
            log.info(message, NULL);
            return;
        }

        Action<REQUEST, RESULTBODY> action = protocolWithCtx.getProtocol();
        ServiceContext<REQUEST> context = protocolWithCtx.getServiceContext();

        ActionLog actionLog = new ActionLog(action);
        ServiceContextLog<REQUEST> contextLog = new ServiceContextLog<>(context != null ? context.getCtx() : null);

        ProtocolWithCtxLog<REQUEST> protocolWithCtxLog = new ProtocolWithCtxLog<>(actionLog, contextLog);

        if (log.isDebugEnabled()) {
            log.debug(message, protocolWithCtxLog);
        } else {
            log.info(message, protocolWithCtxLog.getNotSensitiveData());
        }
    }

    public void log(String message, RESULT result) {
        ResultLog<RESULT> resultLog = new ResultLog<>(result);

        if (log.isDebugEnabled()) {
            log.debug(message, resultLog);
        } else {
            log.info(message, resultLog.getNotSensitiveData());
        }
    }
}

package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.result.body.ResultBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class FacadeOptionalService<REQUEST extends FacadeServiceableGetter, RESULT extends ResultBody, ACTION extends Action<REQUEST, RESULT>> extends FacadeService<REQUEST, RESULT, ACTION>  {

    public FacadeOptionalService(ProtocolAction action, Map<String, ? extends ACTION> actionProviders, ProtocolSelector selector,
                                 ServiceContextProvider provider, ProtocolResultHandler handler, TransactionTemplate txTemplate) {
        super(action, actionProviders, selector, provider, handler, txTemplate);
    }

    @Override
    public CompletableFuture<FacadeResult<RESULT>> execute(REQUEST request) {
        ProtocolWithCtx<ACTION, REQUEST> protocolWithCtx = createContextAndFindProtocol(request);

        if (null == protocolWithCtx) {
            throw new IllegalStateException("Unable to create context");
        }

        CompletableFuture<Result<RESULT>> result;
        if (protocolWithCtx.getProtocol() != null) {
            result = execute(protocolWithCtx.getProtocol(), protocolWithCtx.getServiceContext());
        } else {
            result = supplyNoProtocolResult(request, protocolWithCtx.getServiceContext());
        }

        return handleProtocolResult(request, protocolWithCtx, result);
    }

    @Override
    protected InternalContext<REQUEST, ACTION> selectAndSetProtocolTo(InternalContext<REQUEST, ACTION> ctx) {
        return selector.selectProtocolFor(
                ctx,
                action,
                actionProviders
        ).orElse(ctx);
    }

    protected CompletableFuture<Result<RESULT>> supplyNoProtocolResult(REQUEST request, ServiceContext<REQUEST> ctx) {
        return CompletableFuture.completedFuture(null);
    }
}

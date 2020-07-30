package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.result.body.ResultBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.exceptions.NoProtocolRegisteredException;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public abstract class FacadeService<REQUEST extends FacadeServiceableGetter, RESULT extends ResultBody, ACTION extends Action<REQUEST, RESULT>> {

    private final ProtocolAction action;
    private final Map<String, ? extends ACTION> actionProviders;
    private final ProtocolSelector selector;
    private final ServiceContextProvider provider;
    private final ProtocolResultHandler handler;
    private final TransactionTemplate txTemplate;

    public CompletableFuture<FacadeResult<RESULT>> execute(REQUEST request) {
        ProtocolWithCtx<ACTION, REQUEST> protocolWithCtx = txTemplate.execute(callback -> {
            InternalContext<REQUEST, ACTION> contextWithoutProtocol = contextFor(request);
            InternalContext<REQUEST, ACTION> ctx = selectAndSetProtocolTo(contextWithoutProtocol);
            ServiceContext<REQUEST> serviceContext = addRequestScopedFor(request, ctx);
            return new ProtocolWithCtx<>(ctx.getAction(), serviceContext);
        });
        if (protocolWithCtx == null || protocolWithCtx.getProtocol() == null) {
            throw new NoProtocolRegisteredException("can't create service context or determine protocol");
        }

        CompletableFuture<Result<RESULT>> result = execute(protocolWithCtx.getProtocol(), protocolWithCtx.getServiceContext());
        // This one must exist in decoupled transaction
        return result.thenApply(
                res -> handleResult(
                        res,
                        request.getFacadeServiceable(),
                        protocolWithCtx.getServiceContext()
                )
        );
    }

    protected InternalContext<REQUEST, ACTION> contextFor(REQUEST request) {
        return provider.provide(request);
    }

    protected ServiceContext<REQUEST> addRequestScopedFor(REQUEST request, InternalContext<REQUEST, ACTION> ctx) {
        return provider.provideRequestScoped(request, ctx);
    }

    protected InternalContext<REQUEST, ACTION> selectAndSetProtocolTo(InternalContext<REQUEST, ACTION> ctx) {
        return selector.selectProtocolFor(
                ctx,
                action,
                actionProviders
        );
    }

    protected FacadeResult<RESULT> handleResult(Result<RESULT> result, FacadeServiceableRequest request, ServiceContext<REQUEST> ctx) {
        return handler.handleResult(result, request, ctx);
    }

    protected CompletableFuture<Result<RESULT>> execute(ACTION protocol, ServiceContext<REQUEST> ctx) {
        return protocol.execute(ctx);
    }
}

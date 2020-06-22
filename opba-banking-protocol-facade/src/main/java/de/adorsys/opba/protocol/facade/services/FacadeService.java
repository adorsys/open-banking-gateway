package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.result.body.ResultBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public abstract class FacadeService<I extends FacadeServiceableGetter, O extends ResultBody, A extends Action<I, O>> {

    private final ProtocolAction action;
    private final Map<String, ? extends A> actionProviders;
    private final ProtocolSelector selector;
    private final ServiceContextProvider provider;
    private final ProtocolResultHandler handler;
    private final TransactionTemplate txTemplate;

    public CompletableFuture<FacadeResult<O>> execute(I request) {
        ProtocolWithCtx<A, I> protocolWithCtx = txTemplate.execute(callback -> {
            InternalContext<I, A> contextWithoutProtocol = contextFor(request);
            InternalContext<I, A> ctx = selectAndSetProtocolTo(contextWithoutProtocol);
            ServiceContext<I> serviceContext = addRequestScopedFor(request, ctx);
            return new ProtocolWithCtx<>(ctx.getAction(), serviceContext);
        });
        if (protocolWithCtx == null) {
            throw new NullPointerException("can't create service context or determine protocol");
        }

        CompletableFuture<Result<O>> result = execute(protocolWithCtx.getProtocol(), protocolWithCtx.getServiceContext());
        // This one must exist in decoupled transaction
        return result.thenApply(
                res -> handleResult(
                        res,
                        request.getFacadeServiceable(),
                        protocolWithCtx.getServiceContext()
                )
        );
    }

    protected InternalContext<I, A> contextFor(I request) {
        return provider.provide(request);
    }

    protected ServiceContext<I> addRequestScopedFor(I request, InternalContext<I, A> ctx) {
        return provider.provideRequestScoped(request, ctx);
    }

    protected InternalContext<I, A> selectAndSetProtocolTo(InternalContext<I, A> ctx) {
        return selector.selectProtocolFor(
                ctx,
                action,
                actionProviders
        );
    }

    protected FacadeResult<O> handleResult(Result<O> result, FacadeServiceableRequest request, ServiceContext<I> ctx) {
        return handler.handleResult(result, request, ctx);
    }

    protected CompletableFuture<Result<O>> execute(A protocol, ServiceContext<I> ctx) {
        return protocol.execute(ctx);
    }
}

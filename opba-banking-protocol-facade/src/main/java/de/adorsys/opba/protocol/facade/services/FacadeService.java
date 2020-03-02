package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.result.body.ResultBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public abstract class FacadeService<I extends FacadeServiceableGetter, O extends ResultBody, A extends Action<I, O>> {

    private final ProtocolAction action;
    private final Map<String, ? extends A> actionProviders;
    private final ProtocolSelector selector;
    private final ServiceContextProvider provider;
    private final ProtocolResultHandler handler;

    @Transactional
    public CompletableFuture<FacadeResult<O>> execute(I request) {
        ServiceContext<I> ctx = contextFor(request);
        A protocol = selectAndSetProtocolTo(ctx);
        CompletableFuture<Result<O>> result = execute(protocol, ctx);

        return result.thenApply(
                res -> handleResult(
                        res,
                        request.getFacadeServiceable().getRequestId(),
                        ctx
                )
        );
    }

    protected ServiceContext<I> contextFor(I request) {
        return provider.provide(request);
    }

    protected A selectAndSetProtocolTo(ServiceContext<I> ctx) {
        return selector.selectAndPersistProtocolFor(
                ctx,
                action,
                actionProviders
        );
    }

    protected FacadeResult<O> handleResult(Result<O> result, UUID xRequestId, ServiceContext<I> ctx) {
        return handler.handleResult(result, xRequestId, ctx);
    }

    protected CompletableFuture<Result<O>> execute(A protocol, ServiceContext<I> ctx) {
        return protocol.execute(ctx);
    }
}

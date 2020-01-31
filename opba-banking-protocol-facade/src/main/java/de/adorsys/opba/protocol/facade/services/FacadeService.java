package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public abstract class FacadeService<I extends FacadeServiceableGetter, O, A extends Action<I, O>> {

    private final ProtocolAction action;
    private final Map<String, ? extends A> actionProviders;
    private final ProtocolSelector selector;
    private final ServiceContextProvider provider;
    private final ResultHandler handler;

    @Transactional
    public CompletableFuture<Result<O>> execute(I request) {
        ServiceContext<I> ctx = contextFor(request);
        A protocol = selectProtocol(ctx);
        CompletableFuture<Result<O>> result = protocol.execute(ctx);

        return result.thenApply(
                res -> handleResult(
                        res,
                        ctx.getServiceSessionId()
                )
        );
    }

    protected ServiceContext<I> contextFor(I request) {
        return provider.provide(request);
    }

    protected A selectProtocol(ServiceContext<I> ctx) {
        return selector.protocolFor(
                ctx,
                action,
                actionProviders
        );
    }

    protected Result<O> handleResult(Result<O> result, UUID serviceSessionId) {
        return handler.handleResult(result, serviceSessionId);
    }
}

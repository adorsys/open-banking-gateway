package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.result.body.ResultBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.error.ErrorResult;
import de.adorsys.opba.protocol.api.errors.ReturnableException;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.exceptions.NoProtocolRegisteredException;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import de.adorsys.opba.protocol.facade.util.logresolver.FacadeLogResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Facade service that requires underlying protocol to serve request. Invoking such service without registered protocol,
 * will cause an exception.
 * @param <REQUEST> Request to execute
 * @param <RESULT> Result of the request
 * @param <ACTION> Associated action
 */
@Slf4j
@RequiredArgsConstructor
public abstract class FacadeService<REQUEST extends FacadeServiceableGetter, RESULT extends ResultBody, ACTION extends Action<REQUEST, RESULT>> {

    protected final ProtocolAction action;
    protected final Map<String, ? extends ACTION> actionProviders;
    protected final ProtocolSelector selector;
    protected final ServiceContextProvider provider;
    protected final ProtocolResultHandler handler;
    protected final TransactionTemplate txTemplate;
    protected final FacadeLogResolver logResolver = new FacadeLogResolver(getClass());

    /**
     * Execute the request by passing it to protocol, or throw if protocol is missing.
     * @param request Request to execute
     * @return Result of request execution
     */
    public CompletableFuture<FacadeResult<RESULT>> execute(REQUEST request) {
        ProtocolWithCtx<ACTION, REQUEST> protocolWithCtx = createContextAndFindProtocol(request);

        if (protocolWithCtx == null || protocolWithCtx.getProtocol() == null) {
            throw new NoProtocolRegisteredException("can't create service context or determine protocol");
        }

        CompletableFuture<Result<RESULT>> result = execute(protocolWithCtx.getProtocol(), protocolWithCtx.getServiceContext());
        return handleProtocolResult(request, protocolWithCtx, result);
    }

    protected CompletableFuture<FacadeResult<RESULT>> handleProtocolResult(REQUEST request, ProtocolWithCtx<ACTION, REQUEST> protocolWithCtx, CompletableFuture<Result<RESULT>> result) {
        // This one must exist in decoupled transaction
        return result.thenApply(
                res -> handleResult(
                        res,
                        request.getFacadeServiceable(),
                        protocolWithCtx.getServiceContext()
                )
        );
    }

    @Nullable
    protected ProtocolWithCtx<ACTION, REQUEST> createContextAndFindProtocol(REQUEST request) {
        logResolver.log("execute request({})", request);

        ProtocolWithCtx<ACTION, REQUEST> protocolWithCtx = txTemplate.execute(callback -> {
            InternalContext<REQUEST, ACTION> contextWithoutProtocol = contextFor(request);
            InternalContext<REQUEST, ACTION> ctx = selectAndSetProtocolTo(contextWithoutProtocol);
            ServiceContext<REQUEST> serviceContext = addRequestScopedFor(request, ctx);
            return new ProtocolWithCtx<>(ctx.getAction(), serviceContext);
        });

        logResolver.log("About to execute protocol with context: {}", protocolWithCtx);
        return protocolWithCtx;
    }

    protected InternalContext<REQUEST, ACTION> contextFor(REQUEST request) {
        return provider.provide(request);
    }

    protected ServiceContext<REQUEST> addRequestScopedFor(REQUEST request, InternalContext<REQUEST, ACTION> ctx) {
        return provider.provideRequestScoped(request, ctx);
    }

    protected InternalContext<REQUEST, ACTION> selectAndSetProtocolTo(InternalContext<REQUEST, ACTION> ctx) {
        return selector.requireProtocolFor(
                ctx,
                action,
                actionProviders
        );
    }

    protected FacadeResult<RESULT> handleResult(Result<RESULT> result, FacadeServiceableRequest request, ServiceContext<REQUEST> ctx) {
        logResolver.log("handleResult result({}), request({}), context({})", result, request, ctx);

        return handler.handleResult(result, request, ctx);
    }

    protected CompletableFuture<Result<RESULT>> execute(ACTION protocol, ServiceContext<REQUEST> ctx) {
        try {
            return protocol.execute(ctx);
        } catch (ReturnableException ex) {
            log.error("[{}]/{}/{}", ctx.getRequest().getFacadeServiceable().getRequestId(), ctx.getServiceSessionId(), ctx.getAuthSessionId(), ex);
            return CompletableFuture.completedFuture(new ErrorResult<>(ex.getMessage()));
        } catch (Exception ex) {
            log.error("[{}]/{}/{}", ctx.getRequest().getFacadeServiceable().getRequestId(), ctx.getServiceSessionId(), ctx.getAuthSessionId(), ex);
            return CompletableFuture.completedFuture(new ErrorResult<>(ex.getClass().toString()));
        }
    }
}

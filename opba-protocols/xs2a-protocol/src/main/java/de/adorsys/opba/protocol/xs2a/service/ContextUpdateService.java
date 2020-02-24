package de.adorsys.opba.protocol.xs2a.service;

import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.api.FlowableOptimisticLockingException;
import org.flowable.engine.RuntimeService;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;

/**
 * Service that deals with optimistic lock exceptions when updating context. They happen when API received
 * user input but we received outdated variable from database.
 * Note that due to the how JsonCustomSerializer is implemented this class will always read correct context.
 * If you create and persist Xs2aListAccountsContext it will be always read here as Xs2aListAccountsContext.
 */
@Service
@RequiredArgsConstructor
public class ContextUpdateService {

    private final RuntimeService runtimeService;

    @Retryable(FlowableOptimisticLockingException.class)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T updateContext(String executionId, Function<T, T> updateCtx) {
        T ctx = (T) runtimeService.getVariable(executionId, CONTEXT);
        runtimeService.setVariable(executionId, CONTEXT, updateCtx.apply(ctx));
        return ctx;
    }
}

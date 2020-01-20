package de.adorsys.opba.core.protocol.service;

import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.api.FlowableOptimisticLockingException;
import org.flowable.engine.RuntimeService;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;

/**
 * Service that deals with optimistic lock exceptions when updating context. They happen when API received
 * user input but we received outdated variable from database.
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

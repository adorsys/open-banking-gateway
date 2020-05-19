package de.adorsys.opba.protocol.hbci.entrypoint.helpers;

import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.api.FlowableOptimisticLockingException;
import org.flowable.engine.RuntimeService;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.CONTEXT;

// TODO: Deduplicate
@Service
@RequiredArgsConstructor
public class HbciContextUpdateService {

    private final RuntimeService runtimeService;

    @Retryable(FlowableOptimisticLockingException.class)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T updateContext(String executionId, Function<T, T> updateCtx) {
        T ctx = (T) runtimeService.getVariable(executionId, CONTEXT);
        runtimeService.setVariable(executionId, CONTEXT, updateCtx.apply(ctx));
        return ctx;
    }
}

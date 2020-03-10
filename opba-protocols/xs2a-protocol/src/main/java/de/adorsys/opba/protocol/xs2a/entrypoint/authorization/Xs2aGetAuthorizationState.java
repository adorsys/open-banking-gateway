package de.adorsys.opba.protocol.xs2a.entrypoint.authorization;

import de.adorsys.opba.protocol.api.authorization.GetAuthorizationState;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.ConsentAcquired;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.Redirect;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.Response;
import de.adorsys.opba.protocol.xs2a.entrypoint.OutcomeMapper;
import de.adorsys.opba.protocol.xs2a.entrypoint.authorization.common.AuthorizationContinuationService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.xs2a.service.xs2a.context.ContextMode.MOCK_REAL_CALLS;

@Service("xs2aGetAuthorizationState")
@RequiredArgsConstructor
public class Xs2aGetAuthorizationState implements GetAuthorizationState {

    private final AuthorizationContinuationService continuationService;
    private final RuntimeService runtimeService;

    @Override
    public CompletableFuture<Result<AuthStateBody>> execute(ServiceContext<AuthorizationRequest> serviceContext) {
        String executionId = serviceContext.getAuthContext();
        ensureCallWillBeIdempotentForProcess(executionId);
        return continuationService.handleAuthorizationProcessContinuation(executionId, OnlyValidationPassMapper::new);
    }

    /**
     * The call is not going to be idempotent as it will change database state, redirect code etc. But in terms of
     * process itself we need to be sure that it is idempotent as it is used for GET request.
     * Process in 'MOCK' (validation) state should be always idempotent.
     */
    private void ensureCallWillBeIdempotentForProcess(String executionId) {
        BaseContext ctx = (BaseContext) runtimeService.getVariable(executionId, CONTEXT);

        if (MOCK_REAL_CALLS != ctx.getMode()) {
            throw new IllegalStateException("Unable to get authorization state - non-idempotent mode: " + ctx.getMode());
        }
    }

    private static class OnlyValidationPassMapper extends OutcomeMapper<AuthStateBody> {

        OnlyValidationPassMapper(CompletableFuture<Result<AuthStateBody>> channel) {
            super(channel, null);
        }

        @Override
        public void onSuccess(Response responseResult) {
            throw new IllegalStateException("Unexpected SUCCESS state");
        }

        @Override
        public void onRedirect(Redirect redirectResult) {
            throw new IllegalStateException("Unexpected REDIRECT state");
        }

        @Override
        public void onConsentAcquired(ConsentAcquired acquired) {
            throw new IllegalStateException("Unexpected CONSENT ACQUIRED state");
        }

        @Override
        public void onError() {
            throw new IllegalStateException("Unexpected ERROR state");
        }
    }
}

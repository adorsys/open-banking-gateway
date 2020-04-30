package de.adorsys.opba.protocol.xs2a.entrypoint;

import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.AuthorizationRequiredResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ConsentAcquiredResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.error.ErrorResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ConsentAcquired;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.Redirect;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ValidationProblem;
import de.adorsys.opba.protocol.xs2a.entrypoint.dto.ContextBasedValidationErrorResult;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Mapper to convert from internal protocol result to facade facing protocol result.
 * @param <T>
 */
@RequiredArgsConstructor
public class OutcomeMapper<T> {

    protected final CompletableFuture<Result<T>> channel;
    protected final Function<ProcessResponse, T> extractBodyOnSuccess;
    protected final DtoMapper<Set<ValidationIssue>, Set<ValidationError>> errorMapper;

    public void onSuccess(ProcessResponse responseResult) {
        channel.complete(new SuccessResult<>(extractBodyOnSuccess.apply(responseResult)));
    }

    public void onRedirect(Redirect redirectResult) {
        channel.complete(
                new ContextBasedAuthorizationRequiredResult<>(
                        redirectResult.getRedirectUri(), redirectResult.getExecutionId()
                )
        );
    }

    public void onValidationProblem(ValidationProblem problem) {
        channel.complete(
                new ContextBasedValidationErrorResult(
                    problem.getProvideMoreParamsDialog(),
                    problem.getExecutionId(),
                    new AuthStateBody(null, errorMapper.map(problem.getIssues()), null, null)
                )
        );
    }

    public void onConsentAcquired(ConsentAcquired acquired) {
        channel.complete(
            // Facade knows redirection target
            new ConsentAcquiredResult<>(null, null)
        );
    }

    public void onError() {
        channel.complete(new ErrorResult<>());
    }

    private static class ContextBasedAuthorizationRequiredResult<T> extends AuthorizationRequiredResult<T, Object> {

        private final String executionId;

        ContextBasedAuthorizationRequiredResult(URI redirectionTo, String executionId) {
            super(redirectionTo, null);
            this.executionId = executionId;
        }

        @Override
        public String authContext() {
            return executionId;
        }
    }

}

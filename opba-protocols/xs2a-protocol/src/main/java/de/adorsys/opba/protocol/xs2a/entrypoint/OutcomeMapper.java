package de.adorsys.opba.protocol.xs2a.entrypoint;

import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.AuthorizationRequiredResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ValidationErrorResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.error.ErrorResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.ConsentAcquiredResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.ConsentAcquired;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.Redirect;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.Response;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.ValidationIssue;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@RequiredArgsConstructor
public class OutcomeMapper<T> {

    private final CompletableFuture<Result<T>> channel;
    private final Function<Response, T> extractBody;

    public void onSuccess(Response responseResult) {
        channel.complete(new SuccessResult<>(extractBody.apply(responseResult)));
    }

    public void onRedirect(Redirect redirectResult) {
        channel.complete(
                new ContextBasedAuthorizationRequiredResult<>(
                        redirectResult.getRedirectUri(), redirectResult.getExecutionId()
                )
        );
    }

    public void onValidationProblem(ValidationIssue validationIssue) {
        channel.complete(
                new ContextBasedValidationErrorResult<>(
                        validationIssue.getProvideMoreParamsDialog(), validationIssue.getExecutionId()
                )
        );
    }

    public void onConsentAcquired(ConsentAcquired acquired) {
        channel.complete(
            new ConsentAcquiredResult<>()
        );
    }

    public void onError() {
        channel.complete(new ErrorResult<>());
    }

    private static class ContextBasedAuthorizationRequiredResult<T> extends AuthorizationRequiredResult<T> {

        private final String executionId;

        ContextBasedAuthorizationRequiredResult(URI redirectionTo, String executionId) {
            super(redirectionTo);
            this.executionId = executionId;
        }

        @Override
        public String authContext() {
            return executionId;
        }
    }

    private static class ContextBasedValidationErrorResult<T> extends ValidationErrorResult<T> {

        private final String executionId;

        ContextBasedValidationErrorResult(URI redirectionTo, String executionId) {
            super(redirectionTo);
            this.executionId = executionId;
        }

        @Override
        public String authContext() {
            return executionId;
        }
    }
}

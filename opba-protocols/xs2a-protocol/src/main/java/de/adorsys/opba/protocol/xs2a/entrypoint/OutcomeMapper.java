package de.adorsys.opba.protocol.xs2a.entrypoint;

import de.adorsys.opba.protocol.api.dto.result.AuthorizationRequiredResult;
import de.adorsys.opba.protocol.api.dto.result.ErrorResult;
import de.adorsys.opba.protocol.api.dto.result.Result;
import de.adorsys.opba.protocol.api.dto.result.SuccessResult;
import de.adorsys.opba.protocol.api.dto.result.ValidationErrorResult;
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
        channel.complete(new AuthorizationRequiredResult<>(URI.create(redirectResult.getRedirectUri())));
    }

    public void onValidationProblem(ValidationIssue validationIssue) {
        channel.complete(new ValidationErrorResult<>(validationIssue.getProvideMoreParamsDialog()));
    }

    public void onError() {
        channel.complete(new ErrorResult<>());
    }
}

package de.adorsys.opba.protocol.xs2a.service.xs2a.oauth2;

import de.adorsys.xs2a.adapter.service.exception.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * Special service to parse ASPSP authorization responses on Oauth2 required condition. Used mostly for Oauth2-pre-step.
 */
@Service
@RequiredArgsConstructor
public class Oauth2RequiredErrorSink {

    /**
     * Swallows 403-status code exceptions that indicate Oauth2 is required.
     * @param tryAuthorize Authorization function to call
     * @param onFail Fallback function to call if retryable exception occurred.
     */
    public void swallowOauth2AuthorizationError(Runnable tryAuthorize, Consumer<ErrorResponseException> onFail) {
        try {
            tryAuthorize.run();
        } catch (ErrorResponseException ex) {
            rethrowIfNotAuthorizationErrorCode(ex);
            onFail.accept(ex);
        }
    }

    private void rethrowIfNotAuthorizationErrorCode(ErrorResponseException ex) {
        if (!ex.getErrorResponse().isPresent()
                || HttpStatus.FORBIDDEN.value() != ex.getStatusCode()
                || HttpStatus.UNAUTHORIZED.value() != ex.getStatusCode()) {
            throw ex;
        }
    }
}

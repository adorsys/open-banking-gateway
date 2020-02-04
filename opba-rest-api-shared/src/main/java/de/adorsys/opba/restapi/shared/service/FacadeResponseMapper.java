package de.adorsys.opba.restapi.shared.service;

import de.adorsys.opba.protocol.api.dto.result.ErrorResult;
import de.adorsys.opba.protocol.api.dto.result.RedirectionResult;
import de.adorsys.opba.protocol.api.dto.result.Result;
import de.adorsys.opba.protocol.api.dto.result.SuccessResult;
import de.adorsys.opba.restapi.shared.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacadeResponseMapper {

    public <T, E> ResponseEntity<?> translate(Result<T> result, ErrorResultMapper<ErrorResult, E> toError) {
        if (result instanceof RedirectionResult) {
            return handleRedirect((RedirectionResult) result);
        }

        if (result instanceof ErrorResult) {
            return handleError((ErrorResult) result, toError);
        }

        if (result instanceof SuccessResult) {
            return handleSuccess((SuccessResult<T>) result);
        }

        throw new IllegalArgumentException("Unknown result type: " + result.getClass());
    }

    private ResponseEntity<?> handleRedirect(RedirectionResult result) {
        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.X_REQUEST_ID, "FOO")
                .header(HttpHeaders.PSU_CONSENT_SESSION, "BAR")
                .location(result.getRedirectionTo())
                .body("Please use redirect link in Location header");
    }

    private <E> ResponseEntity<E> handleError(ErrorResult result, ErrorResultMapper<ErrorResult, E> toError) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(toError.map(result));
    }

    private <T> ResponseEntity<T> handleSuccess(SuccessResult<T> result) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result.getBody());
    }
}

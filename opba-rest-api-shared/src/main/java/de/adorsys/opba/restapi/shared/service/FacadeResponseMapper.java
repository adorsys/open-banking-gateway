package de.adorsys.opba.restapi.shared.service;

import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeErrorResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeRedirectResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeSuccessResult;
import de.adorsys.opba.restapi.shared.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacadeResponseMapper {

    public <T, E> ResponseEntity<?> translate(FacadeResult<T> result, ErrorResultMapper<FacadeErrorResult, E> toError) {
        if (result instanceof FacadeRedirectResult) {
            return handleRedirect((FacadeRedirectResult) result);
        }

        if (result instanceof FacadeErrorResult) {
            return handleError((FacadeErrorResult) result, toError);
        }

        if (result instanceof FacadeSuccessResult) {
            return handleSuccess((FacadeSuccessResult<T>) result);
        }

        throw new IllegalArgumentException("Unknown result type: " + result.getClass());
    }

    private ResponseEntity<?> handleRedirect(FacadeRedirectResult result) {
        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.X_REQUEST_ID, "FOO")
                .header(HttpHeaders.PSU_CONSENT_SESSION, "BAR")
                .location(result.getRedirectionTo())
                .body("Please use redirect link in Location header");
    }

    private <E> ResponseEntity<E> handleError(FacadeErrorResult result, ErrorResultMapper<FacadeErrorResult, E> toError) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(toError.map(result));
    }

    private <T> ResponseEntity<T> handleSuccess(FacadeSuccessResult<T> result) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result.getBody());
    }
}

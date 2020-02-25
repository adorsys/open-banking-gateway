package de.adorsys.opba.restapi.shared.service;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectErrorResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeResultRedirectable;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeStartAuthorizationResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.staticres.FacadeSuccessResult;
import de.adorsys.opba.tppbankingapi.mapper.FacadeToRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.adorsys.opba.restapi.shared.HttpHeaders.AUTHORIZATION_SESSION_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.PSU_CONSENT_SESSION;
import static de.adorsys.opba.restapi.shared.HttpHeaders.REDIRECT_CODE;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SEE_OTHER;

@Service
@RequiredArgsConstructor
public class FacadeResponseMapper {

    public <T> ResponseEntity<?> translate(FacadeResult<T> result, FacadeToRestMapper<?, T> mapper) {
        if (result instanceof FacadeRedirectErrorResult) {
            return handleError((FacadeRedirectErrorResult) result);
        }

        if (result instanceof FacadeResultRedirectable) {
            return handleRedirect((FacadeResultRedirectable) result);
        }

        if (result instanceof FacadeSuccessResult) {
            return handleSuccess((FacadeSuccessResult<T>) result, mapper);
        }

        throw new IllegalArgumentException("Unknown result type: " + result.getClass());
    }

    private ResponseEntity<?> handleRedirect(FacadeResultRedirectable<?> result) {
        if (result instanceof FacadeStartAuthorizationResult) {
            return handleInitialAuthorizationRedirect((FacadeStartAuthorizationResult) result);
        }

        return defaultHandleRedirect(result);
    }

    private ResponseEntity<?> handleInitialAuthorizationRedirect(FacadeStartAuthorizationResult<?> result) {
        ResponseEntity.BodyBuilder response = putDefaultHeaders(result, ResponseEntity.status(ACCEPTED));
        putExtraRedirectHeaders(result, response);
        return responseForRedirection(result, response);
    }

    private ResponseEntity<?> defaultHandleRedirect(FacadeResultRedirectable<?> result) {
        ResponseEntity.BodyBuilder response = putDefaultHeaders(result, ResponseEntity.status(SEE_OTHER));
        putExtraRedirectHeaders(result, response);
        return responseForRedirection(result, response);
    }

    private ResponseEntity<Map<String, String>> responseForRedirection(FacadeResultRedirectable<?> result, ResponseEntity.BodyBuilder response) {
        return response
            .header(AUTHORIZATION_SESSION_ID, result.getAuthorizationSessionId())
            .header(REDIRECT_CODE, result.getRedirectCode())
            .header(PSU_CONSENT_SESSION, "BAR")
            .location(result.getRedirectionTo())
            .body(ImmutableMap.of("msg", "Please use redirect link in 'Location' header"));
    }

    private <E> ResponseEntity<E> handleError(FacadeRedirectErrorResult<?> result) {
        ResponseEntity.BodyBuilder response = putDefaultHeaders(result, ResponseEntity.status(SEE_OTHER));
        return putExtraRedirectHeaders(result, response).build();
    }

    private <T> ResponseEntity<?> handleSuccess(FacadeSuccessResult<T> result, FacadeToRestMapper<?, T> mapper) {
        ResponseEntity.BodyBuilder response = putDefaultHeaders(result, ResponseEntity.status(OK));
        return  response.body(mapper.mapFromFacadeToRest(result.getBody()));
    }

    private ResponseEntity.BodyBuilder putDefaultHeaders(FacadeResult<?> result, ResponseEntity.BodyBuilder builder) {
        builder
                .header(X_REQUEST_ID, null == result.getXRequestId() ? null : result.getXRequestId().toString())
                .header(SERVICE_SESSION_ID, result.getServiceSessionId());
        return builder;
    }

    private ResponseEntity.BodyBuilder putExtraRedirectHeaders(FacadeResultRedirectable<?> result, ResponseEntity.BodyBuilder builder) {
        result.getHeaders().forEach(builder::header);
        return builder;
    }
}

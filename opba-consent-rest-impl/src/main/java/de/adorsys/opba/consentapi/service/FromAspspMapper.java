package de.adorsys.opba.consentapi.service;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectErrorResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeResultRedirectable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.adorsys.opba.restapi.shared.HttpHeaders.AUTHORIZATION_SESSION_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.PSU_CONSENT_SESSION;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_XSRF_TOKEN;
import static org.springframework.http.HttpStatus.ACCEPTED;

@Service
public class FromAspspMapper {

    public <F> ResponseEntity<?> translate(FacadeResult<F> result) {
        if (result instanceof FacadeRedirectErrorResult) {
            return handleError((FacadeRedirectErrorResult) result);
        }

        if (result instanceof FacadeResultRedirectable) {
            return handleRedirect((FacadeResultRedirectable) result);
        }

        throw new IllegalArgumentException("Unknown result type: " + result.getClass());
    }

    protected ResponseEntity<?> handleRedirect(FacadeResultRedirectable<?, ?> result) {
        return doHandleRedirect(result);
    }

    protected ResponseEntity<?> doHandleRedirect(FacadeResultRedirectable<?, ?> result) {
        ResponseEntity.BodyBuilder response = putDefaultHeaders(result, ResponseEntity.status(ACCEPTED));
        putExtraRedirectHeaders(result, response);
        response.body(result.getCause());
        return responseForRedirection(result, response);
    }

    protected ResponseEntity<Map<String, String>> responseForRedirection(FacadeResultRedirectable<?, ?> result, ResponseEntity.BodyBuilder response) {
        return response
            .header(AUTHORIZATION_SESSION_ID, result.getAuthorizationSessionId())
            .header(X_XSRF_TOKEN, result.getRedirectCode())
            .header(PSU_CONSENT_SESSION, "BAR")
            .location(result.getRedirectionTo())
            .body(ImmutableMap.of("msg", "Please use redirect link in 'Location' header"));
    }

    protected <E> ResponseEntity<E> handleError(FacadeRedirectErrorResult<?, ?> result) {
        ResponseEntity.BodyBuilder response = putDefaultHeaders(result, ResponseEntity.status(ACCEPTED));
        return putExtraRedirectHeaders(result, response).build();
    }

    protected ResponseEntity.BodyBuilder putDefaultHeaders(FacadeResult<?> result, ResponseEntity.BodyBuilder builder) {
        builder
            .header(X_REQUEST_ID, null == result.getXRequestId() ? null : result.getXRequestId().toString())
            .header(SERVICE_SESSION_ID, result.getServiceSessionId());
        return builder;
    }

    protected ResponseEntity.BodyBuilder putExtraRedirectHeaders(FacadeResultRedirectable<?, ?> result, ResponseEntity.BodyBuilder builder) {
        result.getHeaders().forEach(builder::header);
        return builder;
    }
}


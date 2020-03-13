package de.adorsys.opba.restapi.shared.service;

import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeResultRedirectable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.restapi.shared.HttpHeaders.AUTHORIZATION_SESSION_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.REDIRECT_CODE;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.OK;

@Service
public class RedirectionOnlyToOkMapper {

    public <F> ResponseEntity<?> translate(FacadeResult<F> result) {
        if (result instanceof FacadeResultRedirectable) {
            return handleRedirect((FacadeResultRedirectable) result);
        }

        throw new IllegalArgumentException("Unknown result type: " + result.getClass());
    }

    protected ResponseEntity<?> handleRedirect(FacadeResultRedirectable<?, ?> result) {
        return doHandleRedirect(result);
    }

    protected ResponseEntity<?> doHandleRedirect(FacadeResultRedirectable<?, ?> result) {
        ResponseEntity.BodyBuilder response = putDefaultHeaders(result, ResponseEntity.status(OK));
        putExtraRedirectHeaders(result, response);
        response.body(result.getCause());
        return responseForRedirection(result, response);
    }

    protected ResponseEntity<?> responseForRedirection(FacadeResultRedirectable<?, ?> result, ResponseEntity.BodyBuilder response) {
         response
            .header(AUTHORIZATION_SESSION_ID, result.getAuthorizationSessionId())
            .header(REDIRECT_CODE, result.getRedirectCode());

         if (null != result.getRedirectionTo()) {
             response.header(LOCATION, result.getRedirectionTo().toASCIIString());
         }

         return response.body(result.getCause());
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

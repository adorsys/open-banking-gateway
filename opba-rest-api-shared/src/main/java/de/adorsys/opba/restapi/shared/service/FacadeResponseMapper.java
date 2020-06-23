package de.adorsys.opba.restapi.shared.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.api.security.internal.config.CookieProperties;
import de.adorsys.opba.api.security.internal.service.CookieBuilderTemplate;
import de.adorsys.opba.protocol.facade.config.auth.UriExpandConst;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectErrorResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeResultRedirectable;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeStartAuthorizationResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.staticres.FacadeSuccessResult;
import de.adorsys.opba.restapi.shared.mapper.FacadeResponseBodyToRestBodyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static de.adorsys.opba.restapi.shared.HttpHeaders.AUTHORIZATION_SESSION_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.COOKIE_TTL;
import static de.adorsys.opba.restapi.shared.HttpHeaders.PSU_CONSENT_SESSION;
import static de.adorsys.opba.restapi.shared.HttpHeaders.REDIRECT_CODE;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.X_REQUEST_ID;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@Service
@RequiredArgsConstructor
public class FacadeResponseMapper {
    private final CookieProperties cookieProperties;
    private final CookieBuilderTemplate cookieBuilderTemplate;

    public <T, F> ResponseEntity<?> translate(FacadeResult<F> result, FacadeResponseBodyToRestBodyMapper<T, F> mapper) {
        if (result instanceof FacadeRedirectErrorResult) {
            return handleError((FacadeRedirectErrorResult) result);
        }

        if (result instanceof FacadeResultRedirectable) {
            return handleRedirect((FacadeResultRedirectable) result);
        }

        if (result instanceof FacadeSuccessResult) {
            return handleSuccess((FacadeSuccessResult<F>) result, mapper);
        }

        throw new IllegalArgumentException("Unknown result type: " + result.getClass());
    }

    protected ResponseEntity<?> handleRedirect(FacadeResultRedirectable<?, ?> result) {
        if (result instanceof FacadeStartAuthorizationResult) {
            return handleInitialAuthorizationRedirect((FacadeStartAuthorizationResult) result);
        }

        return doHandleRedirect(result);
    }

    protected ResponseEntity<?> handleInitialAuthorizationRedirect(FacadeStartAuthorizationResult<?, ?> result) {
        ResponseEntity.BodyBuilder response = putDefaultHeaders(result, ResponseEntity.status(ACCEPTED));
        putExtraRedirectHeaders(result, response);
        response.body(result.getCause());
        return responseForRedirection(result, response);
    }

    protected ResponseEntity<?> doHandleRedirect(FacadeResultRedirectable<?, ?> result) {
        ResponseEntity.BodyBuilder response = putDefaultHeaders(result, ResponseEntity.status(ACCEPTED));
        putExtraRedirectHeaders(result, response);
        response.body(result.getCause());
        return responseForRedirection(result, response);
    }

    protected ResponseEntity<Map<String, String>> responseForRedirection(FacadeResultRedirectable<?, ?> result, ResponseEntity.BodyBuilder response) {
        return setCookieHeaders(result, response)
                       .header(AUTHORIZATION_SESSION_ID, result.getAuthorizationSessionId())
                       .header(REDIRECT_CODE, result.getRedirectCode())
                       .header(PSU_CONSENT_SESSION, "BAR")
                       .location(result.getRedirectionTo())
                       .body(ImmutableMap.of("msg", "Please use redirect link in 'Location' header"));
    }

    protected <E> ResponseEntity<E> handleError(FacadeRedirectErrorResult<?, ?> result) {
        ResponseEntity.BodyBuilder response = putDefaultHeaders(result, ResponseEntity.status(ACCEPTED));
        return putExtraRedirectHeaders(result, response).build();
    }

    protected <T, F> ResponseEntity<T> handleSuccess(FacadeSuccessResult<F> result, FacadeResponseBodyToRestBodyMapper<T, F> mapper) {
        ResponseEntity.BodyBuilder response = putDefaultHeaders(result, ResponseEntity.status(OK));
        return response.body(mapper.map(result.getBody()));
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

    private ResponseEntity.BodyBuilder setCookieHeaders(FacadeResultRedirectable<?, ?> result, ResponseEntity.BodyBuilder builder) {
        if (Strings.isNullOrEmpty(result.getToken())) {
            return builder;
        }

        String redirectPath = fromAspspRedirectPath(result.getAuthorizationSessionId(), result.getRedirectCode());
        ResponseCookie responseCookie = cookieBuilderTemplate.builder(result.getToken(),
                                                                     redirectPath,
                                                                     cookieProperties.getRedirectMaxAge())
                                               .build();

        builder.header(COOKIE_TTL, Long.toString(cookieProperties.getRedirectMaxAge().getSeconds()))
                .header(SET_COOKIE, responseCookie.toString());

        return builder;
    }

    public String fromAspspRedirectPath(String authorizationId, String redirectState) {
        return UriComponentsBuilder.fromPath(cookieProperties.getRedirectPathTemplate())
                       .buildAndExpand(ImmutableMap.of(UriExpandConst.AUTHORIZATION_SESSION_ID, authorizationId,
                                                       UriExpandConst.REDIRECT_STATE, redirectState))
                       .toUriString();
    }
}

package de.adorsys.opba.fintech.impl.service;


import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.SessionRepository;
import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
import de.adorsys.opba.fintech.impl.tppclients.Consts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.FIN_TECH_AUTH_ID;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.X_REQUEST_ID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionLogicService {
    private static final String AUTH_ID_VARIABLE = "\\{auth-id}";

    private final SessionRepository sessionRepository;
    private final CookieConfigProperties cookieConfigProperties;
    private final RestRequestContext restRequestContext;


    @Transactional
    public HttpHeaders login(UserEntity userEntity, String xsrfToken) {
        SessionEntity sessionEntity = new SessionEntity(userEntity, cookieConfigProperties.getSessioncookie().getMaxAge(), null);
        sessionEntity.setSessionCookieValue(SessionEntity.createSessionCookieValue(xsrfToken));
        sessionRepository.save(sessionEntity);

        String cookieAsString = ResponseCookie.from(Consts.COOKIE_SESSION_COOKIE_NAME, sessionEntity.getSessionCookieValue())
                .httpOnly(cookieConfigProperties.getSessioncookie().isHttpOnly())
                .sameSite(cookieConfigProperties.getSessioncookie().getSameSite())
                .secure(cookieConfigProperties.getSessioncookie().isSecure())
                .path(cookieConfigProperties.getSessioncookie().getPath())
                .maxAge(cookieConfigProperties.getSessioncookie().getMaxAge())
                .build()
                .toString();

        log.info("create new session for user {} with cookie {}", userEntity.getLoginUserName(), cookieAsString);

        HttpHeaders httpHeaders = createHttpHeaders(cookieAsString, xsrfToken);
        httpHeaders.add(Consts.HEADER_SESSION_MAX_AGE, "" + cookieConfigProperties.getSessioncookie().getMaxAge());
        return httpHeaders;

    }

    @Transactional
    public HttpHeaders startRedirect(UserEntity userEntity, String authId) {
        Long parentSessionId = sessionRepository.findBySessionCookieValue(restRequestContext.getSessionCookieValue()).get().getId();
        SessionEntity sessionEntity = new SessionEntity(userEntity, cookieConfigProperties.getRedirectcookie().getMaxAge(), parentSessionId);
        String xsrfToken = UUID.randomUUID().toString();
        sessionEntity.setSessionCookieValue(SessionEntity.createSessionCookieValue(xsrfToken));
        sessionRepository.save(sessionEntity);

        String path = cookieConfigProperties.getRedirectcookie().getPath();
        if (!path.matches("(.*)" + AUTH_ID_VARIABLE + "(.*)")) {
            throw new RuntimeException("programming error. path " + path + " does not match with " + AUTH_ID_VARIABLE);
        }
        path = path.replaceAll(AUTH_ID_VARIABLE, authId);

        String cookieAsString = ResponseCookie.from(Consts.COOKIE_REDIRECT_COOKIE_NAME, sessionEntity.getSessionCookieValue())
                .httpOnly(cookieConfigProperties.getRedirectcookie().isHttpOnly())
                .sameSite(cookieConfigProperties.getRedirectcookie().getSameSite())
                .secure(cookieConfigProperties.getRedirectcookie().isSecure())
                .path(path)
                .maxAge(cookieConfigProperties.getRedirectcookie().getMaxAge())
                .build()
                .toString();

        HttpHeaders responseHeaders = createHttpHeaders(cookieAsString, xsrfToken);
        responseHeaders.add(Consts.HEADER_REDIRECT_MAX_AGE, "" + cookieConfigProperties.getRedirectcookie().getMaxAge());
        responseHeaders.add(FIN_TECH_AUTH_ID, authId);

        log.info("create new redirect session for user {} with cookie {}", userEntity.getLoginUserName(), cookieAsString);

        return responseHeaders;

    }

    @Transactional
    public HttpHeaders finishRedirect() {
        SessionEntity sessionEntity = sessionRepository.findBySessionCookieValue(restRequestContext.getRedirectCookieValue()).get();
        if (sessionEntity.getParentSession().equals(null)) {
            throw new RuntimeException("programming error, expected session for cookie value " + restRequestContext.getRedirectCookieValue() + " to be redirect session");
        }
        SessionEntity parentSession = sessionRepository.findById(sessionEntity.getParentSession()).get();
        sessionRepository.delete(sessionEntity);
        OffsetDateTime now = OffsetDateTime.now();
        log.info("old session will be renewed with new duration time");
        parentSession.setValidUntil(now.plusSeconds(cookieConfigProperties.getSessioncookie().getMaxAge()));
        sessionRepository.save(parentSession);

        String cookieAsString = ResponseCookie.from(Consts.COOKIE_SESSION_COOKIE_NAME, parentSession.getSessionCookieValue())
                .httpOnly(cookieConfigProperties.getSessioncookie().isHttpOnly())
                .sameSite(cookieConfigProperties.getSessioncookie().getSameSite())
                .secure(cookieConfigProperties.getSessioncookie().isSecure())
                .path(cookieConfigProperties.getSessioncookie().getPath())
                .maxAge(cookieConfigProperties.getSessioncookie().getMaxAge())
                .build()
                .toString();

        HttpHeaders httpHeaders = createHttpHeaders(cookieAsString, null);
        httpHeaders.set(X_REQUEST_ID, restRequestContext.getRequestId());
        httpHeaders.add(Consts.HEADER_SESSION_MAX_AGE, "" + cookieConfigProperties.getSessioncookie().getMaxAge());
        return httpHeaders;
    }

    @Transactional
    public boolean isSessionAuthorized() {
        log.info(restRequestContext.toString());
        if (restRequestContext.getSessionCookieValue() == null || restRequestContext.getXsrfTokenHeaderField() == null || restRequestContext.getRequestId() == null) {
            log.error("unauthorized call {} due to missing {}", restRequestContext.getUri(),
                    restRequestContext.getSessionCookieValue() == null
                            ? "session cookie" : restRequestContext.getXsrfTokenHeaderField() == null ? "XSRFToken" : "RequestID");
            return false;
        }

        // first check token with session without any DB
        String sessionCookieValue = restRequestContext.getSessionCookieValue();
        SessionEntity.validateSessionCookieValue(sessionCookieValue, restRequestContext.getXsrfTokenHeaderField());

        // now check that this sessionCookie is really known in DB
        Optional<SessionEntity> optionalSessionEntity = sessionRepository.findBySessionCookieValue(sessionCookieValue);
        if (!optionalSessionEntity.isPresent()) {
            log.error("session cookie might be old. However it is not found in DB and thus not valid {} ", sessionCookieValue);
            return false;
        }

        log.info("renew max age for session and persist it");
        optionalSessionEntity.get().setValidUntil(OffsetDateTime.now().plusSeconds(cookieConfigProperties.getSessioncookie().getMaxAge()));
        sessionRepository.save(optionalSessionEntity.get());

        return true;
    }

    @Transactional
    public boolean isRedirectAuthorized() {
        log.info(restRequestContext.toString());
        if (restRequestContext.getRedirectCookieValue() == null || restRequestContext.getXsrfTokenHeaderField() == null || restRequestContext.getRequestId() == null) {
            log.error("unauthorized redirect call {} due to missing {}", restRequestContext.getUri(),
                    restRequestContext.getRedirectCookieValue() == null
                            ? "redirect cookie" : restRequestContext.getXsrfTokenHeaderField() == null ? "XSRFToken" : "RequestID");
            return false;
        }

        // first check token with session without any DB
        String redirectCookieValue = restRequestContext.getRedirectCookieValue();
        SessionEntity.validateSessionCookieValue(redirectCookieValue, restRequestContext.getXsrfTokenHeaderField());

        // now check that this sessionCookie is really known in DB
        Optional<SessionEntity> optionalSessionEntity = sessionRepository.findBySessionCookieValue(redirectCookieValue);
        if (!optionalSessionEntity.isPresent()) {
            log.error("redirect cookie might be old. However it is not found in DB and thus not valid {} ", redirectCookieValue);
            return false;
        }

        return true;
    }


    public SessionEntity getSession() {
        String sessionCookieValue = restRequestContext.getSessionCookieValue();
        return sessionRepository.findBySessionCookieValue(sessionCookieValue).get();
    }


    public ResponseEntity addSessionMaxAgeToHeader(ResponseEntity e) {
        List<String> headers = e.getHeaders().get(Consts.HEADER_SESSION_MAX_AGE);
        if (headers == null) {
            log.info("add header field for max age to response");
            HttpHeaders h = HttpHeaders.writableHttpHeaders(e.getHeaders());
            h.set(Consts.HEADER_SESSION_MAX_AGE, "" + cookieConfigProperties.getSessioncookie().getMaxAge());
            return new ResponseEntity<>(e.getBody(), h, e.getStatusCode());
        }
        log.info("header field for max age is already inresponse");
        return e;
    }

    private HttpHeaders createHttpHeaders(String cookieAsString, String xsrfToken) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_REQUEST_ID, restRequestContext.getRequestId());
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookieAsString);
        if (xsrfToken != null) {
            responseHeaders.add(Consts.HEADER_XSRF_TOKEN, xsrfToken);
        }
        return responseHeaders;

    }

}

package de.adorsys.opba.fintech.impl.service;


import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
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

import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.FIN_TECH_AUTH_ID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionLogicService {
    private static final String AUTH_ID_VARIABLE = "\\{auth-id}";

    private final SessionRepository sessionRepository;
    private final CookieConfigProperties cookieConfigProperties;
    private final RestRequestContext restRequestContext;


    @Transactional
    public HttpHeaders login(UserEntity userEntity) {
        log.info("==> login {} ", restRequestContext);
        SessionEntity sessionEntity = new SessionEntity(userEntity, cookieConfigProperties.getSessioncookie().getMaxAge(), null);
        String sessionXsrfToken = UUID.randomUUID().toString();
        sessionEntity.setSessionCookieValue(SessionEntity.createSessionCookieValue(sessionXsrfToken));
        sessionRepository.save(sessionEntity);
        log.info("created new session for user {}", userEntity.getLoginUserName());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, getSessionCookieAsStringForHeader(sessionEntity));
        responseHeaders.set(Consts.HEADER_SESSION_MAX_AGE, "" + cookieConfigProperties.getSessioncookie().getMaxAge());
        responseHeaders.add(Consts.HEADER_XSRF_TOKEN, sessionXsrfToken);
        return responseHeaders;

    }

    @Transactional
    public HttpHeaders startRedirect(UserEntity userEntity, String authId) {
        SessionEntity sessionEntity = sessionRepository.findBySessionCookieValue(restRequestContext.getSessionCookieValue()).get();
        SessionEntity redirectEntity = new SessionEntity(userEntity, cookieConfigProperties.getRedirectcookie().getMaxAge(), sessionEntity.getId());
        String redirectXsrfToken = UUID.randomUUID().toString();
        redirectEntity.setSessionCookieValue(SessionEntity.createSessionCookieValue(redirectXsrfToken));
        sessionRepository.save(redirectEntity);
        log.info("created new redirect session for user {}", userEntity.getLoginUserName());

        List<String> cookies = new ArrayList<>();
        cookies.add(getRedirectCookieAsStringForHeader(redirectEntity, authId));
        cookies.add(getSessionCookieAsStringForHeader(sessionEntity));
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.addAll(HttpHeaders.SET_COOKIE, cookies);
        responseHeaders.add(Consts.HEADER_XSRF_TOKEN, redirectXsrfToken);
        responseHeaders.add(Consts.HEADER_REDIRECT_MAX_AGE, "" + cookieConfigProperties.getRedirectcookie().getMaxAge());
        responseHeaders.set(Consts.HEADER_SESSION_MAX_AGE, "" + cookieConfigProperties.getSessioncookie().getMaxAge());
        responseHeaders.add(FIN_TECH_AUTH_ID, authId);
        return responseHeaders;

    }

    @Transactional
    public HttpHeaders finishRedirect() {
        SessionEntity redirectSessionEntity = sessionRepository.findBySessionCookieValue(restRequestContext.getRedirectCookieValue()).get();
        if (redirectSessionEntity.getParentSession().equals(null)) {
            throw new RuntimeException("programming error, expected session for cookie value " + restRequestContext.getRedirectCookieValue() + " to be redirect session");
        }
        SessionEntity sessionEntity = sessionRepository.findById(redirectSessionEntity.getParentSession()).get();
        sessionRepository.delete(redirectSessionEntity);
        OffsetDateTime now = OffsetDateTime.now();
        sessionEntity.setValidUntil(now.plusSeconds(cookieConfigProperties.getSessioncookie().getMaxAge()));
        sessionRepository.save(sessionEntity);

        log.info("renewed old session for user {}", sessionEntity.getUserEntity().getLoginUserName());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, getSessionCookieAsStringForHeader(sessionEntity));
        responseHeaders.set(Consts.HEADER_SESSION_MAX_AGE, "" + cookieConfigProperties.getSessioncookie().getMaxAge());
        return responseHeaders;
    }

    @Transactional
    public boolean isSessionAuthorized() {
        log.info("==> is authorized session {} ", restRequestContext);
        if (restRequestContext.getSessionCookieValue() == null || restRequestContext.getXsrfTokenHeaderField() == null || restRequestContext.getRequestId() == null) {
            log.error("unauthorized call {} due to missing {}", restRequestContext.getUri(),
                    restRequestContext.getSessionCookieValue() == null
                            ? "session cookie" : restRequestContext.getXsrfTokenHeaderField() == null ? "XSRFToken" : "RequestID");
            return false;
        }

        // first check token with session without any DB
        String sessionCookieValue = restRequestContext.getSessionCookieValue();
        SessionEntity.validateSessionCookieValue(sessionCookieValue, restRequestContext.getXsrfTokenHeaderField(), restRequestContext);

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
        log.info("==> is authorized redirect session {} ", restRequestContext);
        if (restRequestContext.getRedirectCookieValue() == null || restRequestContext.getXsrfTokenHeaderField() == null || restRequestContext.getRequestId() == null) {
            log.error("unauthorized redirect call {} due to missing {}", restRequestContext.getUri(),
                    restRequestContext.getRedirectCookieValue() == null
                            ? "redirect cookie" : restRequestContext.getXsrfTokenHeaderField() == null ? "XSRFToken" : "RequestID");
            return false;
        }

        // first check token with session without any DB
        String redirectCookieValue = restRequestContext.getRedirectCookieValue();
        SessionEntity.validateSessionCookieValue(redirectCookieValue, restRequestContext.getXsrfTokenHeaderField(), restRequestContext);

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
        if (headers != null) {
            log.info("response already contains max age for session of uri {}", restRequestContext.getUri());
            return e;
        }
        String sessionCookieValue = restRequestContext.getSessionCookieValue();
        if (sessionCookieValue == null) {
            throw new RuntimeException("did expect session cookie to exist for " + restRequestContext.getUri());
        }
        log.debug("add renewed session cookie and header field for max age to response {}", restRequestContext.getUri());
        HttpHeaders h = HttpHeaders.writableHttpHeaders(e.getHeaders());
        h.set(Consts.HEADER_SESSION_MAX_AGE, "" + cookieConfigProperties.getSessioncookie().getMaxAge());
        h.add(HttpHeaders.SET_COOKIE, getSessionCookieAsStringForHeader(sessionCookieValue));
        return new ResponseEntity<>(e.getBody(), h, e.getStatusCode());
    }

    private String getSessionCookieAsStringForHeader(SessionEntity sessionEntity) {
        return getSessionCookieAsStringForHeader(sessionEntity.getSessionCookieValue());
    }

    private String getSessionCookieAsStringForHeader(String sessionCookieValue) {
        String cookieAsString = ResponseCookie.from(Consts.COOKIE_SESSION_COOKIE_NAME, sessionCookieValue)
                .httpOnly(cookieConfigProperties.getSessioncookie().isHttpOnly())
                .sameSite(cookieConfigProperties.getSessioncookie().getSameSite())
                .secure(cookieConfigProperties.getSessioncookie().isSecure())
                .path(cookieConfigProperties.getSessioncookie().getPath())
                .maxAge(cookieConfigProperties.getSessioncookie().getMaxAge())
                .build()
                .toString();
        return cookieAsString;
    }

    private String getRedirectCookieAsStringForHeader(SessionEntity redirectSessionEntity, String authId) {
        String path = cookieConfigProperties.getRedirectcookie().getPath();
        if (!path.matches("(.*)" + AUTH_ID_VARIABLE + "(.*)")) {
            throw new RuntimeException("programming error. path " + path + " does not match with " + AUTH_ID_VARIABLE);
        }
        path = path.replaceAll(AUTH_ID_VARIABLE, authId);
        return ResponseCookie.from(Consts.COOKIE_REDIRECT_COOKIE_NAME, redirectSessionEntity.getSessionCookieValue())
                .httpOnly(cookieConfigProperties.getRedirectcookie().isHttpOnly())
                .sameSite(cookieConfigProperties.getRedirectcookie().getSameSite())
                .secure(cookieConfigProperties.getRedirectcookie().isSecure())
                .path(path)
                .maxAge(cookieConfigProperties.getRedirectcookie().getMaxAge())
                .build()
                .toString();
    }
}

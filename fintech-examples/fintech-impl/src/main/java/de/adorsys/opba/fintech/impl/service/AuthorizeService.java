package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.SessionRepository;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
import de.adorsys.opba.fintech.impl.properties.CookieConfigPropertiesSpecific;
import de.adorsys.opba.fintech.impl.tppclients.Consts;
import de.adorsys.opba.fintech.impl.tppclients.SessionCookieType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.X_REQUEST_ID;

/**
 * This is just a dummy authorization.
 * All users are accepted. Password allways has to be 1234, otherwise login fails
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthorizeService {
    private static final String AUTH_ID_VARIABLE = "\\{auth-id}";

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RestRequestContext restRequestContext;
    private final CookieConfigProperties cookieConfigProperties;

    /**
     * @param loginRequest
     * @return empty, if user not found or password not valid. otherwise optional of userprofile
     */
    @Transactional
    public Optional<SessionEntity> login(LoginRequest loginRequest, String xsrfToken) {
        // this is for demo only. all users are allowed. But password has to be 1234
        // otherwise login is not possible
        generateUserIfUserDoesNotExistYet(loginRequest);

        // find user by id
        Optional<UserEntity> optionalUserEntity = userRepository.findById(loginRequest.getUsername());
        if (!optionalUserEntity.isPresent()) {
            // user not found
            return Optional.empty();
        }

        if (!optionalUserEntity.get().getPassword().equals(loginRequest.getPassword())) {
            // wrong password
            return Optional.empty();
        }

        // password is ok, so log in
        log.info("login for user {}", optionalUserEntity.get().getLoginUserName());

        // new session is created, even if an old one exists
        SessionEntity sessionEntity = new SessionEntity(optionalUserEntity.get(), cookieConfigProperties.getSessioncookie().getMaxAge());
        sessionEntity.setSessionCookieValue(SessionEntity.createSessionCookieValue(xsrfToken));
        optionalUserEntity.get().addLogin(OffsetDateTime.now());
//        optionalUserEntity.get().getSessions().add(sessionEntity);

        sessionRepository.save(sessionEntity);
        return Optional.of(sessionEntity);
    }

    @Transactional
    public HttpHeaders modifySessionEntityAndCreateNewAuthHeader(String xRequestID, SessionEntity sessionEntity, String xsrfToken,
                                                                 CookieConfigProperties cookieProps, SessionCookieType sessionCookieType) {
        sessionEntity.setSessionCookieValue(SessionEntity.createSessionCookieValue(xsrfToken));
        sessionRepository.save(sessionEntity);

        List<String> cookieValues = new ArrayList<>();
        int maxAge = 0;

        if (sessionCookieType.equals(SessionCookieType.REDIRECT)) {
            // simply by passing the session cookie two times, the old will be deleted
            cookieValues.add(createSessionCookieString(sessionEntity, cookieProps.getSessioncookie(), cookieProps.getSessioncookie().getPath(), 0));

            String path = cookieProps.getRedirectcookie().getPath();
            if (path.matches("(.*)" + AUTH_ID_VARIABLE + "(.*)")) {
                path = path.replaceAll(AUTH_ID_VARIABLE, sessionEntity.getAuthId());
            }
            cookieValues.add(createSessionCookieString(sessionEntity, cookieProps.getRedirectcookie(), path, cookieProps.getRedirectcookie().getMaxAge()));

            maxAge = cookieProps.getRedirectcookie().getMaxAge();
        } else {
            // simply by passing the session cookie two times, the old will be deleted
            cookieValues.add(createSessionCookieString(sessionEntity, cookieProps.getSessioncookie(), cookieProps.getSessioncookie().getPath(), 0));

            cookieValues.add(createSessionCookieString(sessionEntity, cookieProps.getSessioncookie(), cookieProps.getSessioncookie().getPath(), cookieProps.getSessioncookie().getMaxAge()));
            maxAge = cookieProps.getSessioncookie().getMaxAge();
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_REQUEST_ID, xRequestID);

        responseHeaders.addAll(HttpHeaders.SET_COOKIE, cookieValues);
        String xsrfTokenString = xsrfToken + "; Max-Age=" + maxAge;
        responseHeaders.add(Consts.HEADER_XSRF_TOKEN, xsrfTokenString);
        log.info("HEADER xsrf token is replaced with new token : {}", xsrfTokenString);
        for (String cookie : cookieValues) {
            log.info("COOKIE session is replaced with new cookie   : {}", cookie);
        }

        return responseHeaders;
    }

    private String createSessionCookieString(SessionEntity sessionEntity, CookieConfigPropertiesSpecific cookieProps, String path, int maxAge) {
        return ResponseCookie.from(Consts.COOKIE_SESSION_COOKIE_NAME, sessionEntity.getSessionCookieValue())
                .httpOnly(cookieProps.isHttpOnly())
                .sameSite(cookieProps.getSameSite())
                .secure(cookieProps.isSecure())
                .path(path)
                .maxAge(maxAge)
                .build()
                .toString();
    }

    @Transactional
    public SessionEntity updateUserSession(SessionEntity sessionEntity) {
        return sessionRepository.save(sessionEntity);
    }

    private void generateUserIfUserDoesNotExistYet(LoginRequest loginRequest) {
        if (userRepository.findById(loginRequest.getUsername()).isPresent()) {
            return;
        }
        userRepository.save(
                UserEntity.builder()
                        .loginUserName(loginRequest.getUsername())
                        .fintechUserId(createID(loginRequest.getUsername()))
                        .password(loginRequest.getPassword())
                        .build());
    }

    private String createID(String username) {
        return new String(Hex.encode(username.getBytes()));
    }

    @Transactional
    public boolean isAuthorized() {
        log.info(restRequestContext.toString());
        if (restRequestContext.getSessionCookieValue() == null || restRequestContext.getXsrfTokenHeaderField() == null || restRequestContext.getRequestId() == null) {
            log.error("unauthorized call due to missing {}",
                    restRequestContext.getSessionCookieValue() == null
                            ? "session cookie" : restRequestContext.getXsrfTokenHeaderField() == null ? "XSRFToken" : "RequestID");
            return false;
        }

        // first check token with session without any DB
        String sessionCookieValue = restRequestContext.getSessionCookieValue();
        SessionEntity.validateSessionCookieValue(sessionCookieValue, restRequestContext.getXsrfTokenHeaderField());

        // now check that this sessionCookie is really known in DB
        Optional<SessionEntity> optionalSessionEntity = sessionRepository.findBySessionCookieValue(restRequestContext.getSessionCookieValue());
        if (!optionalSessionEntity.isPresent()) {
            log.error("session cookie might be old. However it is not found in DB and thus not valid {} ", restRequestContext.getSessionCookieValue());
            return false;
        }

        return true;
    }


    @Transactional
    public void logout() {
        SessionEntity sessionEntity = getSession();
        log.info("logout for user {}", sessionEntity.getUserEntity().getLoginUserName());
        sessionRepository.delete(sessionEntity);
    }

    public SessionEntity getSession() {
        String sessionCookieValue = restRequestContext.getSessionCookieValue();
        return sessionRepository.findBySessionCookieValue(sessionCookieValue).get();
    }
}

package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
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
    private static final String UNIVERSAL_PASSWORD = "1234";
    private static final String AUTH_ID_VARIABLE = "\\{auth-id}";

    private final UserRepository userRepository;
    private final RestRequestContext restRequestContext;

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
        Optional<SessionEntity> optionalUserEntity = userRepository.findById(loginRequest.getUsername());
        if (!optionalUserEntity.isPresent()) {
            return Optional.empty();
        }

        SessionEntity sessionEntity = optionalUserEntity.get();
        if (!sessionEntity.getPassword().equals(loginRequest.getPassword())) {
            return Optional.empty();
        }

        log.info("login for user {}", optionalUserEntity.get().getLoginUserName());

        // password is ok, so log in

        // delete old cookies, if available
        sessionEntity.setSessionCookieValue(SessionEntity.createSessionCookieValue(sessionEntity.getFintechUserId(), xsrfToken));

        sessionEntity.addLogin(OffsetDateTime.now());

        userRepository.save(sessionEntity);
        return Optional.of(sessionEntity);
    }

    @Transactional
    public HttpHeaders modifySessionEntityAndCreateNewAuthHeader(String xRequestID, SessionEntity sessionEntity, String xsrfToken,
                                                                 CookieConfigProperties cookieProps, SessionCookieType sessionCookieType) {
        String oldSessionCookie = sessionEntity.getSessionCookieValue();
        sessionEntity.setSessionCookieValue(SessionEntity.createSessionCookieValue(sessionEntity.getFintechUserId(), xsrfToken));
        userRepository.save(sessionEntity);

        List<String> cookieValues = new ArrayList<>();
        int maxAge = 0;

        // when redirect cookie should be set, old session cookie will be reset
        if (sessionCookieType.equals(SessionCookieType.REDIRECT)) {
            {
                String path = cookieProps.getRedirectcookie().getPath();
                if (path.matches("(.*)" + AUTH_ID_VARIABLE + "(.*)")) {
                    path = path.replaceAll(AUTH_ID_VARIABLE, sessionEntity.getAuthId());
                }

                String sessionCookieString = ResponseCookie.from(Consts.COOKIE_SESSION_COOKIE_NAME, sessionEntity.getSessionCookieValue())
                        .httpOnly(cookieProps.getRedirectcookie().isHttpOnly())
                        .sameSite(cookieProps.getRedirectcookie().getSameSite())
                        .secure(cookieProps.getRedirectcookie().isSecure())
                        .path(path)
                        .maxAge(cookieProps.getRedirectcookie().getMaxAge())
                        .build().toString();
                cookieValues.add(sessionCookieString);
            }
            /*
            if (oldSessionCookie != null) {
                String sessionCookieString = ResponseCookie.from(Consts.COOKIE_SESSION_COOKIE_NAME, oldSessionCookie)
                        .httpOnly(cookieProps.getSessioncookie().isHttpOnly())
                        .sameSite(cookieProps.getSessioncookie().getSameSite())
                        .secure(cookieProps.getSessioncookie().isSecure())
                        .path(cookieProps.getSessioncookie().getPath())
                        .maxAge(0)
                        .build().toString();
                cookieValues.add(sessionCookieString);
            }
            */

            maxAge = cookieProps.getRedirectcookie().getMaxAge();
        } else {
            String sessionCookieString = ResponseCookie.from(Consts.COOKIE_SESSION_COOKIE_NAME, sessionEntity.getSessionCookieValue())
                    .httpOnly(cookieProps.getSessioncookie().isHttpOnly())
                    .sameSite(cookieProps.getSessioncookie().getSameSite())
                    .secure(cookieProps.getSessioncookie().isSecure())
                    .path(cookieProps.getSessioncookie().getPath())
                    .maxAge(cookieProps.getSessioncookie().getMaxAge())
                    .build().toString();
            cookieValues.add(sessionCookieString);
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

    @Transactional
    public SessionEntity updateUserSession(SessionEntity sessionEntity) {
        return userRepository.save(sessionEntity);
    }

    private void generateUserIfUserDoesNotExistYet(LoginRequest loginRequest) {
        if (userRepository.findById(loginRequest.getUsername()).isPresent()) {
            return;
        }
        userRepository.save(
                SessionEntity.builder()
                        .loginUserName(loginRequest.getUsername())
                        .fintechUserId(createID(loginRequest.getUsername()))
                        .password(UNIVERSAL_PASSWORD)
                        .consentConfirmed(false)
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
        Optional<SessionEntity> optionalUserEntity = userRepository.findBySessionCookieValue(restRequestContext.getSessionCookieValue());
        if (!optionalUserEntity.isPresent()) {
            log.error("session cookie might be old. However it is not found in DB and thus not valid {} ", restRequestContext.getSessionCookieValue());
            return false;
        }

        // now make sure, session is known to server
        return optionalUserEntity.get().getSessionCookieValue().equals(sessionCookieValue);
    }


    @Transactional
    public void logout() {
        SessionEntity sessionEntity = getSession();
        log.info("logout for user {}", sessionEntity.getLoginUserName());
        sessionEntity.setSessionCookieValue(null);
    }

    public SessionEntity getSession() {
        String sessionCookieValue = restRequestContext.getSessionCookieValue();
        return userRepository.findBySessionCookieValue(sessionCookieValue).get();
    }
}

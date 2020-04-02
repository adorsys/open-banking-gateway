package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.CookieEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
import de.adorsys.opba.fintech.impl.tppclients.Consts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

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

    private final UserRepository userRepository;
    private final CookieConfigProperties cookieConfigProperties;
    private final RestRequestContext restRequestContext;

    /**
     * @param loginRequest
     * @return empty, if user not found or password not valid. otherwise optional of userprofile
     */
    @Transactional
    public Optional<SessionEntity> login(LoginRequest loginRequest) {
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
        sessionEntity.setXsrfToken(UUID.randomUUID().toString());

        // delete old cookies, if available
        sessionEntity.setSessionCookie(null);
        sessionEntity.setXsrfToken(sessionEntity.getXsrfToken());
        sessionEntity.setSessionCookieValue(SessionEntity.createSessionCookieValue(sessionEntity.getFintechUserId(), sessionEntity.getXsrfToken()));

        sessionEntity.addLogin(OffsetDateTime.now());

        userRepository.save(sessionEntity);
        return Optional.of(sessionEntity);
    }

    public HttpHeaders fillWithAuthorizationHeaders(ContextInformation contextInformation, SessionEntity sessionEntity) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_REQUEST_ID, contextInformation.getXRequestID().toString());
        log.info("set response cookie attributes to {}", cookieConfigProperties.toString());

        CookieEntity sessionCookie = sessionEntity.getSessionCookie();
        String sessionCookieString = ResponseCookie.from(sessionCookie.getName(), sessionCookie.getValue())
                .httpOnly(cookieConfigProperties.getSessioncookie().isHttpOnly())
                .sameSite(cookieConfigProperties.getSessioncookie().getSameSite())
                .secure(cookieConfigProperties.getSessioncookie().isSecure())
                .path(cookieConfigProperties.getSessioncookie().getPath())
                .maxAge(cookieConfigProperties.getSessioncookie().getMaxAge())
                .build().toString();
        responseHeaders.add(HttpHeaders.SET_COOKIE, sessionCookieString);
        responseHeaders.add(Consts.HEADER_XSRF_TOKEN, sessionEntity.getXsrfToken());
        return responseHeaders;
    }

    public SessionEntity getByXsrfToken(String xsrfToken) {
        return userRepository.findByXsrfToken(xsrfToken).get();
    }

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
        String xsrfToken = restRequestContext.getXsrfTokenHeaderField();
        Optional<SessionEntity> optionalUserEntity = userRepository.findByXsrfToken(xsrfToken);
        if (!optionalUserEntity.isPresent()) {
            log.debug("XSRF-TOKEN {} is unknown", xsrfToken);
            return false;
        }

        String sessionCookieValue = restRequestContext.getSessionCookieValue();
        SessionEntity.validateSessionCookieValue(sessionCookieValue, xsrfToken);
        return optionalUserEntity.get().getSessionCookie().getValue().equals(sessionCookieValue);
    }


    @Transactional
    public void logout(String xsrfToken, String sessionCookieContent) {
        Optional<SessionEntity> optionalUserEntity = userRepository.findByXsrfToken(xsrfToken);
        log.info("logout for user {}", optionalUserEntity.get().getLoginUserName());
        userRepository.deleteByXsrfToken(xsrfToken);
    }
}

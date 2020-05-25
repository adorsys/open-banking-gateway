package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.LoginRepository;
import de.adorsys.opba.fintech.impl.database.repositories.SessionRepository;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * This is just a dummy authorization.
 * All users are accepted. Password allways has to be 1234, otherwise login fails
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthorizeService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final LoginRepository loginRepository;
    private final RestRequestContext restRequestContext;

    /**
     * @param loginRequest
     * @return empty, if user not found or password not valid. otherwise optional of userprofile
     */
    @Transactional
    public Optional<UserEntity> login(LoginRequest loginRequest) {
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

        return optionalUserEntity;
    }

    @Transactional
    public boolean isSessionAuthorized() {
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
        Optional<SessionEntity> optionalSessionEntity = sessionRepository.findBySessionCookieValue(sessionCookieValue);
        if (!optionalSessionEntity.isPresent()) {
            log.error("session cookie might be old. However it is not found in DB and thus not valid {} ", sessionCookieValue);
            return false;
        }

        return true;
    }

    @Transactional
    public boolean isRedirectAuthorized() {
        log.info(restRequestContext.toString());
        if (restRequestContext.getRedirectCookieValue() == null || restRequestContext.getXsrfTokenHeaderField() == null || restRequestContext.getRequestId() == null) {
            log.error("unauthorized call due to missing {}",
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


    @Transactional
    public void logout() {
        SessionEntity sessionEntity = getSession();
        log.info("logout for user {}", sessionEntity.getUserEntity().getLoginUserName());
        sessionRepository.delete(sessionEntity);
    }

    private void generateUserIfUserDoesNotExistYet(LoginRequest loginRequest) {
        if (userRepository.existsById(loginRequest.getUsername())) {
            log.info("User {} exists ", loginRequest.getUsername());
            return;
        }
        log.info("create on the fly user {}", loginRequest.getUsername());

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


    public SessionEntity getSession() {
        String sessionCookieValue = restRequestContext.getSessionCookieValue();
        return sessionRepository.findBySessionCookieValue(sessionCookieValue).get();
    }

}

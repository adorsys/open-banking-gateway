package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * This is just a dummy authorization.
 * All users are accepted. Password allways has to be 1234, otherwise login fails
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthorizeService {
    private static final boolean CHECK_SESSION_COOKIE_TODO = false;
    private static final String UNIVERSAL_PASSWORD = "1234";

    private final UserRepository userRepository;

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
    public boolean isAuthorized(String xsrfToken, String sessionCookieContent) {
        Optional<SessionEntity> optionalUserEntity = userRepository.findByXsrfToken(xsrfToken);
        if (!optionalUserEntity.isPresent()) {
            log.debug("XSRF-TOKEN {} is unknown", xsrfToken);
            return false;
        }

        if (!CHECK_SESSION_COOKIE_TODO) {
            log.debug("XSRF-TOKEN {} is known", xsrfToken);
            return true;
        }
        SessionEntity.validateSessionCookieValue(sessionCookieContent, xsrfToken);
        return optionalUserEntity.get().getSessionCookie().getValue().equals(sessionCookieContent);
    }


    @Transactional
    public void logout(String xsrfToken, String sessionCookieContent) {
        Optional<SessionEntity> optionalUserEntity = userRepository.findByXsrfToken(xsrfToken);
        log.info("logout for user {}", optionalUserEntity.get().getLoginUserName());
        userRepository.deleteByXsrfToken(xsrfToken);
    }
}

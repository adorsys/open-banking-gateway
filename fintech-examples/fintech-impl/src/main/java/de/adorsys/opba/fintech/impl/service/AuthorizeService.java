package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.impl.database.entities.CookieEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * This is just a dummy authorization.
 * All users are accepted. Password allways has to be 1234, otherwise login fails
 */
@Configuration
public class AuthorizeService {
    private static final boolean CHECK_SESSION_COOKIE_TOO = false;
    private static final String SESSION_COOKIE_NAME = "SESSION-COOKIE";
    private static final String XSRF_TOKEN_COOKIE_NAME = "XSRF-TOKEN";
    private static final String UNIVERSAL_PASSWORD = "1234";

    @Autowired
    UserRepository userRepository;

    /**
     * @param loginRequest
     * @return empty, if user not found or password not valid. otherwise optional of userprofile
     */
    @Transactional
    public Optional<UserEntity> login(LoginRequest loginRequest) {
        // this is for demo only. all users are allowed. But password has to be 1234
        // otherwise login is not possible
        generateUserIfUserDoesNotExistYet(loginRequest);

        // find user by id
        Optional<UserEntity> optionalUserEntity = userRepository.findById(loginRequest.getUsername());
        if (!optionalUserEntity.isPresent()) {
            return Optional.empty();
        }

        UserEntity userEntity = optionalUserEntity.get();
        if (!userEntity.getPassword().equals(loginRequest.getPassword())) {
            return Optional.empty();
        }

        // password is ok, so log in
        userEntity.setXsrfToken(UUID.randomUUID().toString());

        // delete old cookies, if available
        userEntity.setCookies(new ArrayList<>());
        userEntity.addCookie(SESSION_COOKIE_NAME, UUID.randomUUID().toString());
        userEntity.addCookie(XSRF_TOKEN_COOKIE_NAME, userEntity.getXsrfToken());

        userEntity.addLogin(OffsetDateTime.now());
        userRepository.save(userEntity);
        return Optional.of(userEntity);
    }

    private void generateUserIfUserDoesNotExistYet(LoginRequest loginRequest) {
        if (userRepository.findById(loginRequest.getUsername()) != null) {
            return;
        }
        userRepository.save(
                UserEntity.builder()
                        .name(loginRequest.getUsername())
                        .password(UNIVERSAL_PASSWORD)
                        .build());
    }

    @Transactional
    public boolean isAuthorized(String xsrfToken, String sessionCookieContent) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByXsrfToken(xsrfToken);
        if (!optionalUserEntity.isPresent()) {
            return false;
        }

        if (!CHECK_SESSION_COOKIE_TOO) {
            return true;
        }
            for (CookieEntity cookie : optionalUserEntity.get().getCookies()) {
                if (cookie.getName().equals(SESSION_COOKIE_NAME)) {
                    return cookie.getValue().equals(sessionCookieContent);
                }
            }
            return false;
    }
}

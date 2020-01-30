package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.api.model.generated.UserProfile;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * This is just a dummy authorization.
 * All users are accepted. Password allways has to be 1234, otherwise login fails
 */
@Service
public class AuthorizeService {
    private static final String SESSION_COOKIE_NAME = "SESSION-COOKIE";
    private static final String XSRF_TOKEN_COOKIE_NAME = "XSRF-TOKEN";
    private static final String UNIVERSAL_PASSWORD = "1234";
    private final Map<String, UserEntity> userIDtoEntityMap = new HashMap<>();
    private final Map<String, UserEntity> xsrfIDtoEntityMap = new HashMap<>();

    /**
     * @param loginRequest
     * @return empty, if user not found or password not valid. otherwise optional of userprofile
     */
    public Optional<UserEntity> findUser(LoginRequest loginRequest) {
        // this is for demo only. all users are allowed. But password has to be 1234
        // otherwise login is not possible
        generateUserIfUserDoesNotExistYet(loginRequest);

        // is always false, because user should be generated
        if (!userIDtoEntityMap.containsKey(loginRequest.getUsername())) {
            return Optional.empty();
        }

        UserEntity userEntity = userIDtoEntityMap.get(loginRequest.getUsername());
        UserProfile userProfile = userEntity.password.equalsIgnoreCase(loginRequest.getPassword()) ? userEntity.getUserProfile() : null;
        if (userProfile != null) {
            // password matched
            // create new session
            String xsrfToken = UUID.randomUUID().toString();

            userEntity.addCookie(SESSION_COOKIE_NAME, xsrfToken)
                    .addCookie(XSRF_TOKEN_COOKIE_NAME, xsrfToken);

            // Entity will now be found be xrefid too
            xsrfIDtoEntityMap.put(xsrfToken, userEntity);

            // before now returning user profile, last login has to be changed
            userProfile.lastLogin(userEntity.lastLogin);
            userEntity.setLastLogin(OffsetDateTime.now());
            return Optional.of(userEntity);
        }
        // wrong password
        return Optional.empty();

    }

    private void generateUserIfUserDoesNotExistYet(LoginRequest loginRequest) {
        if (userIDtoEntityMap.containsKey(loginRequest.getUsername())) {
            return;
        }
        UserProfile userProfile = new UserProfile();
        userProfile.setName(loginRequest.getUsername());
        userProfile.setLastLogin(null);
        userIDtoEntityMap.put(loginRequest.getUsername(),
                UserEntity.builder().userProfile(userProfile)
                        .password(UNIVERSAL_PASSWORD)
                        .lastLogin(userProfile.getLastLogin())
                        .cookies(new HashMap<>())
                        .build());
    }

    public boolean isAuthorized(String fintechToken) {
        return xsrfIDtoEntityMap.containsKey(fintechToken);
    }

    @Getter
    @Setter
    @Builder
    public static final class UserEntity {
        private final String password;
        private OffsetDateTime lastLogin;
        private final UserProfile userProfile;
        private String xsrfToken;
        private Map<String, String> cookies;

        public UserEntity addCookie(String key, String value) {
            cookies.put(key, value);
            return this;
        }
    }
}

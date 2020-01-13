package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.LoginRequest;
import de.adorsys.opba.fintech.api.model.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
public class AuthorizeService {
    Map<String, UserEntity> users = initUsersMap();

    /**
     * @param loginRequest
     * @return empty, if user not found or password not valid. otherwise optional of userprofile
     */
    public Optional<UserProfile> findUser(LoginRequest loginRequest) {
        if (!users.containsKey(loginRequest.getUsername())) {
            return Optional.empty();
        }
        UserEntity userEntity = users.get(loginRequest.getUsername());
        UserProfile userProfile = userEntity.password.equalsIgnoreCase(loginRequest.getPassword()) ? userEntity.getUserProfile() : null;
        if (userProfile != null) {
            userProfile.lastLogin(userEntity.lastLogin);
            userEntity.setLastLogin(OffsetDateTime.now());
            return Optional.of(userProfile);
        }
        return Optional.empty();

    }

    private Map<String, UserEntity> initUsersMap() {
        Map<String, UserEntity> map = new HashMap<>();

        String[] users = {"peter", "maksym", "francis"};
        for (String user : users) {
            UserProfile userProfile = new UserProfile();
            userProfile.setName(user);
            userProfile.setLastLogin(null);
            map.put(user, new UserEntity("1234", userProfile.getLastLogin(), userProfile));
        }

        return map;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static final class UserEntity {
        private final String password;
        private OffsetDateTime lastLogin;
        private final UserProfile userProfile;
    }
}

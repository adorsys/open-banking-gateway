package de.adorsys.opba.fintech.impl.service.entities;

import de.adorsys.opba.fintech.api.model.generated.UserProfile;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
public class UserEntity {
    private String password;
    private OffsetDateTime lastLogin;
    private UserProfile userProfile;
    private String xsrfToken;
    private Map<String, String> cookies;

    public UserEntity addCookie(String key, String value) {
        cookies.put(key, value);
        return this;
    }
}
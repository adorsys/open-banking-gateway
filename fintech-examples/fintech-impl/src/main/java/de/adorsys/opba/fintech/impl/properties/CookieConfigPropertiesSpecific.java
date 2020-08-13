package de.adorsys.opba.fintech.impl.properties;

import lombok.Data;
import lombok.ToString;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;

@Data
@ToString
public class CookieConfigPropertiesSpecific {
    private static final Integer MAX_AGE_DEFAULT = 300;
    private boolean secure = false;
    private int maxAge = MAX_AGE_DEFAULT;
    private boolean httpOnly = false;
    private String path = "/";

    @Nullable
    private String sameSite;

    public String buildCookie(String cookieName, String cookieValue) {
        return ResponseCookie.from(cookieName, cookieValue)
                .httpOnly(isHttpOnly())
                .sameSite(getSameSite())
                .secure(isSecure())
                .path(getPath())
                .maxAge(getMaxAge())
                .build()
                .toString();
    }
}

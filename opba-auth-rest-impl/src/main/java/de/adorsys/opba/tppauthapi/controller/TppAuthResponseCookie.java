package de.adorsys.opba.tppauthapi.controller;

import de.adorsys.opba.tppauthapi.config.CookieProperties;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;

@Builder
@RequiredArgsConstructor
public class TppAuthResponseCookie {

    private final CookieProperties cookieProperties;
    private final ResponseCookie.ResponseCookieBuilder responseCookieBuilder;

    public String getCookieString() {
        return responseCookieBuilder
                .httpOnly(cookieProperties.isHttpOnly())
                .sameSite(cookieProperties.getSameSite())
                .secure(cookieProperties.isSecure())
                .path(cookieProperties.getPath())
                .maxAge(cookieProperties.getMaxAge())
                .build().toString();
    }
}

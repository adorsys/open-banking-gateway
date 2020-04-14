package de.adorsys.opba.tppauthapi.controller;

import de.adorsys.opba.tppauthapi.config.CookieProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;

@RequiredArgsConstructor
public class TppAuthResponseCookieTemplate {

    private final CookieProperties cookieProperties;

    public ResponseCookie.ResponseCookieBuilder builder(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(cookieProperties.isHttpOnly())
                .sameSite(cookieProperties.getSameSite())
                .secure(cookieProperties.isSecure())
                .path(cookieProperties.getPath())
                .maxAge(cookieProperties.getMaxAge());
    }
}

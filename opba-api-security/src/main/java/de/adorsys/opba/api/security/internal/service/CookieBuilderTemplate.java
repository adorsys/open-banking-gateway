package de.adorsys.opba.api.security.internal.service;

import de.adorsys.opba.api.security.internal.config.CookieProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;

@RequiredArgsConstructor
public class CookieBuilderTemplate {
    private final CookieProperties cookieProperties;

    public ResponseCookie.ResponseCookieBuilder builder(String token) {
        return initBuilder(token)
                       .path(cookieProperties.getPath())
                       .maxAge(cookieProperties.getMaxAge());
    }

    public ResponseCookie.ResponseCookieBuilder builder(String token, String path) {
        return initBuilder(token)
                       .path(path)
                       .maxAge(cookieProperties.getMaxAge());
    }

    public ResponseCookie.ResponseCookieBuilder builder(String token, String path, String domain) {
        ResponseCookie.ResponseCookieBuilder builder = initBuilder(token)
                                                               .path(path)
                                                               .maxAge(cookieProperties.getMaxAge());
        if (null != domain
                    && !domain.isEmpty()) {
            builder.domain(domain);
        }

        return builder;
    }

    public ResponseCookie.ResponseCookieBuilder builder(String token, String path, Duration maxAge) {
        return initBuilder(token)
                       .path(path)
                       .maxAge(maxAge);
    }

    private ResponseCookie.ResponseCookieBuilder initBuilder(String token) {
        return ResponseCookie.from(AUTHORIZATION_SESSION_KEY, token)
                       .httpOnly(cookieProperties.isHttpOnly())
                       .sameSite(cookieProperties.getSameSite())
                       .secure(cookieProperties.isSecure());
    }
}

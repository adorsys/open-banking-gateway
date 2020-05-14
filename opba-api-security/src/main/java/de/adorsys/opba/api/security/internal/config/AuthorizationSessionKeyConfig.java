package de.adorsys.opba.api.security.internal.config;

import de.adorsys.opba.api.security.internal.EnableTokenBasedApiSecurity;
import de.adorsys.opba.api.security.internal.service.TokenBasedAuthService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;

@Configuration
@EnableTokenBasedApiSecurity
@RequiredArgsConstructor
@Getter
@Slf4j
public class AuthorizationSessionKeyConfig {

    private final TokenBasedAuthService authService;

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public AuthorizationSessionKeyFromHttpRequest getAuthorizationSessionKeyFromHttpRequest(HttpServletRequest httpServletRequest) {
        log.debug("Incomming request {}", httpServletRequest.getRequestURI());
        String authCookieValue = Arrays.stream(httpServletRequest.getCookies())
                .filter(it -> AUTHORIZATION_SESSION_KEY.equals(it.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new RuntimeException("programming error: no cookie supplied, filter must be wrong "
                        + httpServletRequest.getMethod() + " " + httpServletRequest.getRequestURI()));
        return new AuthorizationSessionKeyFromHttpRequest(authService.validateTokenAndGetSubject(authCookieValue));
    }

    @RequiredArgsConstructor
    @Getter
    public static class AuthorizationSessionKeyFromHttpRequest {
        private final String key;
    }
}

package de.adorsys.opba.tppauthapi.config;

import de.adorsys.opba.api.security.internal.EnableTokenBasedApiSecurity;
import de.adorsys.opba.api.security.internal.service.TokenBasedAuthService;
import lombok.AllArgsConstructor;
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

import static de.adorsys.opba.restapi.shared.HttpHeaders.AUTHORIZATION_SESSION_KEY;

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
        log.debug("REQUEST COMMING IN {}",  httpServletRequest.getRequestURI());
        String authCookieValue = Arrays.stream(httpServletRequest.getCookies())
                .filter(it -> AUTHORIZATION_SESSION_KEY.equals(it.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new RuntimeException("programming error: no cookie supplied, filter must be wrong"));
        return new AuthorizationSessionKeyFromHttpRequest(authService.validateTokenAndGetSubject(authCookieValue));
    }

    @AllArgsConstructor
    public static class AuthorizationSessionKeyFromHttpRequest {
        private String key;
        public String get() {
            return key;
        }
    }
}
